package unify.nullpointerexception.business.implementation.file;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import unify.nullpointerexception.business.BusinessException;
import unify.nullpointerexception.business.MusicService;
import unify.nullpointerexception.business.PlaylistService;
import unify.nullpointerexception.business.UserService;
import unify.nullpointerexception.domain.Playlist;
import unify.nullpointerexception.domain.Song;
import unify.nullpointerexception.domain.User;

public class FilePlaylistService implements PlaylistService {

    private String playlistFilePath;

    private int newId = -1;

    private final int ID_INDEX = 0, OWNER_ID_INDEX = 1, NAME_INDEX = 2, FIRST_SONG_INDEX = 3;

    private UserService userService;
    private MusicService musicService;

    public FilePlaylistService(String playlistFilePath, UserService userService, MusicService musicService) {

        this.playlistFilePath = playlistFilePath;
        this.userService = userService;
        this.musicService = musicService;

        try {
            // inizializzo newId all'id dell'ultima riga del file + 1
            FileData fileData = FileUtility.readAllRows(playlistFilePath);
            int rowsNumber = fileData.getRows().size();
            newId = Integer.parseInt(fileData.getRows().get(rowsNumber - 1)[ID_INDEX]) + 1;

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public List<Playlist> findAllPlaylistsByUser(User user) throws BusinessException {

        List<Playlist> result = new ArrayList<Playlist>();
        try {
            FileData fileData = FileUtility.readAllRows(playlistFilePath);
            for (String[] columns : fileData.getRows()) {

                // id ; id_creatore ; nome ; id_brano_1 ; ... ; id_brano_n
                if (Integer.parseInt(columns[OWNER_ID_INDEX]) == user.getId()) {
                    Playlist playlist = new Playlist();
                    playlist.setId(Integer.parseInt(columns[ID_INDEX]));
                    playlist.setCreator(user);
                    playlist.setName(columns[NAME_INDEX]);

                    // ciclo necessario perché le canzoni possono essere di numero variabile
                    for (int i = FIRST_SONG_INDEX; i < columns.length; i++) {
                        playlist.getSongs().add(musicService.findMusicById(Integer.parseInt(columns[i])));
                    }

                    result.add(playlist);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(e);
        }

        return result;
    }

    @Override
    public Playlist findPlaylistById(int id) throws BusinessException {

        try {
            FileData fileData = FileUtility.readAllRows(playlistFilePath);
            for (String[] columns : fileData.getRows()) {

                // id ; id_creatore ; nome ; id_brano_1 ; ... ; id_brano_n
                if (Integer.parseInt(columns[ID_INDEX]) == id) {

                    // creo la nuova playlist
                    Playlist playlist = new Playlist(id,
                            userService.findUserById(Integer.parseInt(columns[OWNER_ID_INDEX])), columns[NAME_INDEX]);

                    for (int i = FIRST_SONG_INDEX; i < columns.length; i++) {
                        playlist.getSongs().add(musicService.findMusicById(Integer.parseInt(columns[i])));
                    }
                    return playlist;
                }
            }

            return null;
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(e);
        }
    }

    // metodo necessario per convertire la playlist nel formato adatto al
    // salvataggio su file, ovvero:
    // id ; id_creatore ; nome ; id_brano_1 ; ... ; id_brano_n
    private String playlistToRow(Playlist playlist) {

        String row = playlist.getId() + FileUtility.COLUMN_SEPARATOR + playlist.getCreator().getId()
                + FileUtility.COLUMN_SEPARATOR + playlist.getName();

        for (Song song : playlist.getSongs())
            row += (FileUtility.COLUMN_SEPARATOR + song.getId());

        return row.toString() + '\n';

    }

    @Override
    public void createPlaylist(Playlist playlist) throws BusinessException {
        playlist.setId(newId++);
        FileUtility.appendRow(playlistFilePath, playlistToRow(playlist));
    }

    @Override
    public void editPlaylist(Playlist playlist) throws BusinessException {
        FileUtility.editRowById(playlistFilePath, playlist.getId(), playlistToRow(playlist));
    }

    @Override
    public void deletePlaylist(Playlist playlist) throws BusinessException {
        FileUtility.deleteRowById(playlistFilePath, playlist.getId());
    }

    @Override
    public List<Playlist> getAllPlaylists() throws BusinessException {
        // Questo metodo non è necessario nell'implementazione tramite file
        return null;
    }
}
