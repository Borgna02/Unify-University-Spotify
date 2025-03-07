package unify.nullpointerexception.business.implementation.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import unify.nullpointerexception.business.ArtistsService;
import unify.nullpointerexception.business.BusinessException;
import unify.nullpointerexception.business.GenreService;
import unify.nullpointerexception.business.MusicService;
import unify.nullpointerexception.domain.Artist;
import unify.nullpointerexception.domain.Song;

public class FileMusicService implements MusicService {

    private GenreService genreService;
    private ArtistsService artistsService;

    private String songsFilePath;
    private String albumFilePath;
    private String playlistFilePath;

    private final String MUSIC_PATH = "src/main/resources/canzoni/";
    private final String MUSIC_EXT = ".wav";
    private final String COVER_PATH = "src/main/resources/img/cover_singoli/";
    private final String COVER_EXT = ".png";
    private final String LYRICS_PATH = "src/main/resources/files/lyrics/";
    private final String LYRICS_EXT = ".txt";
    private int ID_INDEX = 0, TITLE_INDEX = 1, GENRE_ID_INDEX = 2, DATE_INDEX = 3, ARTIST_ID_INDEX = 4;

    private byte[] defaultCover;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private int newId = -1;

    public FileMusicService(String songsFilePath, String albumFilePath, String playlistFilePath,
            GenreService genreService, ArtistsService artistService) {

        this.songsFilePath = songsFilePath;
        this.albumFilePath = albumFilePath;
        this.playlistFilePath = playlistFilePath;
        this.genreService = genreService;
        this.artistsService = artistService;

        File f = new File(COVER_PATH + "default_cover" + COVER_EXT);
        if (f.exists()) {

            try {
                defaultCover = Files.readAllBytes(f.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            // inizializzo newId all'id dell'ultima riga del file + 1
            FileData fileData = FileUtility.readAllRows(songsFilePath);
            int rowsNumber = fileData.getRows().size();
            newId = Integer.parseInt(fileData.getRows().get(rowsNumber - 1)[ID_INDEX]) + 1;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Song> findAllMusicByArtist(Artist artist) throws BusinessException {

        List<Song> result = new ArrayList<>();
        try {
            FileData fileData = FileUtility.readAllRows(songsFilePath);
            // id ; titolo ; id_artista ; genere ; anno_uscita -> id;
            // titolo; id_genere; data_uscita;
            // id_artista_TITLE_INDEX;...;id_artista_n
            for (String[] columns : fileData.getRows()) {
                boolean artistTroved = false;
                List<Artist> aList = new ArrayList<Artist>();

                // scorro l'array canzone dall'indice dove iniziano i cantanti
                for (int i = ARTIST_ID_INDEX; i < columns.length; i++) {
                    aList.add(artistsService.findArtistById(Integer.parseInt(columns[i])));
                    if (artist.getId() == Integer.parseInt(columns[i])) {
                        artistTroved = true;
                    }

                }
                // se è stato trovato crea la canzone
                if (artistTroved == true) {
                    Song song = new Song(Integer.parseInt(columns[ID_INDEX]), columns[TITLE_INDEX],
                            LocalDate.parse(columns[DATE_INDEX], formatter),
                            genreService.findGenreById(Integer.parseInt(columns[GENRE_ID_INDEX])));

                    song.setArtists(aList);
                    if (!(result.contains(song)))
                        result.add(song);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(e);
        }

        return result;
    }

    @Override
    public List<Song> findAllMusicByGenreName(String genreName) throws BusinessException {

        List<Song> result = new ArrayList<>();

        try {
            FileData fileData = FileUtility.readAllRows(songsFilePath);
            for (String[] columns : fileData.getRows()) {
                // id ; titolo ; id_artista ; genere ; anno_uscita se id_album == ""
                // allora la canzone è un singolo

                if (genreService.findGenreById(Integer.parseInt(columns[GENRE_ID_INDEX])).getName().equals(genreName)) {
                    Song song = new Song(Integer.parseInt(columns[ID_INDEX]), columns[TITLE_INDEX],
                            LocalDate.parse(columns[DATE_INDEX], formatter),
                            genreService.findGenreById(Integer.parseInt(columns[GENRE_ID_INDEX])));

                    List<Artist> aList = new ArrayList<Artist>();
                    for (int i = ARTIST_ID_INDEX; i < columns.length; i++) {
                        aList.add(artistsService.findArtistById(Integer.parseInt(columns[i])));
                    }
                    song.setArtists(aList);
                    result.add(song);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(e);
        }

        return result;
    }

    @Override
    public List<Song> findAllMusicByGenrePrefix(String genrePrefix) throws BusinessException {

        genrePrefix = genrePrefix.toLowerCase();

        List<Song> result = new ArrayList<>();

        try {
            FileData fileData = FileUtility.readAllRows(songsFilePath);
            for (String[] columns : fileData.getRows()) {
                // id ; titolo ; id_artista ; genere ; anno_uscita se id_album == ""
                // allora la canzone è un singolo

                if (genreService.findGenreById(Integer.parseInt(columns[GENRE_ID_INDEX])).getName().toLowerCase()
                        .contains(genrePrefix)) {
                    Song song = new Song(Integer.parseInt(columns[ID_INDEX]), columns[TITLE_INDEX],
                            LocalDate.parse(columns[DATE_INDEX], formatter),
                            genreService.findGenreById(Integer.parseInt(columns[GENRE_ID_INDEX])));

                    List<Artist> aList = new ArrayList<Artist>();
                    for (int i = ARTIST_ID_INDEX; i < columns.length; i++) {
                        aList.add(artistsService.findArtistById(Integer.parseInt(columns[i])));
                    }
                    song.setArtists(aList);
                    result.add(song);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(e);
        }

        return result;
    }

    @Override
    public List<Song> findAllMusicByPrefix(String prefix) throws BusinessException {

        List<Song> result = new ArrayList<>();

        try {
            FileData fileData = FileUtility.readAllRows(songsFilePath);
            for (String[] columns : fileData.getRows()) {
                // id ; titolo ; id_artista ; genere ; anno_uscita se id_album == ""
                // allora la canzone è un singolo

                if (columns[TITLE_INDEX].toLowerCase().contains(prefix.toLowerCase())) {
                    Song song = new Song(Integer.parseInt(columns[ID_INDEX]), columns[TITLE_INDEX],
                            LocalDate.parse(columns[DATE_INDEX], formatter),
                            genreService.findGenreById(Integer.parseInt(columns[GENRE_ID_INDEX])));

                    List<Artist> aList = new ArrayList<Artist>();
                    for (int i = ARTIST_ID_INDEX; i < columns.length; i++) {
                        aList.add(artistsService.findArtistById(Integer.parseInt(columns[i])));
                    }
                    song.setArtists(aList);
                    result.add(song);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(e);
        }

        return result;
    }

    @Override
    public Song findMusicById(Integer id) throws BusinessException {

        try {
            FileData fileData = FileUtility.readAllRows(songsFilePath);
            for (String[] columns : fileData.getRows()) {

                // id ; titolo ; id_artista ; genere ; anno_uscita se id_album == ""
                // allora la canzone è un singolo
                if (Integer.parseInt(columns[ID_INDEX]) == id) {
                    Song song = new Song(id, columns[TITLE_INDEX], LocalDate.parse(columns[DATE_INDEX], formatter),
                            genreService.findGenreById(Integer.parseInt(columns[GENRE_ID_INDEX])));

                    List<Artist> aList = new ArrayList<Artist>();
                    for (int i = ARTIST_ID_INDEX; i < columns.length; i++) {
                        aList.add(artistsService.findArtistById(Integer.parseInt(columns[i])));
                    }

                    song.setArtists(aList);

                    return song;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(e);
        }
        return null;

    }

    @Override
    public List<Song> getAllSongs() throws BusinessException {

        List<Song> songs = new ArrayList<Song>();

        try {
            FileData fileData = FileUtility.readAllRows(songsFilePath);
            for (String[] columns : fileData.getRows()) {

                // id ; titolo ; id_artista ; genere ; anno_uscita se id_album == ""
                // allora la canzone è un singolo

                Song song = new Song(Integer.parseInt(columns[ID_INDEX]), columns[TITLE_INDEX],
                        LocalDate.parse(columns[DATE_INDEX], formatter),
                        genreService.findGenreById(Integer.parseInt(columns[GENRE_ID_INDEX])));

                List<Artist> aList = new ArrayList<Artist>();
                for (int i = ARTIST_ID_INDEX; i < columns.length; i++) {
                    aList.add(artistsService.findArtistById(Integer.parseInt(columns[i])));
                }
                song.setArtists(aList);

                songs.add(song);
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(e);
        }

        return songs;

    }

    private String songToRow(Song song) {

        /// id ; titolo ; id_artista ; id_genere ; data_uscita

        String row = song.getId() + FileUtility.COLUMN_SEPARATOR + song.getTitle() + FileUtility.COLUMN_SEPARATOR
                + song.getGenre().getId() + FileUtility.COLUMN_SEPARATOR + song.getReleaseDate().format(formatter);

        for (Artist tempArtist : song.getArtists()) {
            row += (FileUtility.COLUMN_SEPARATOR + tempArtist.getId());
        }

        return row + '\n';
    }

    @Override
    public void createSong(Song newSong, String newText, byte[] newTrack, byte[] newSongCover)
            throws BusinessException {

        newSong.setId(newId++);

        if (newTrack != null)
            setTrack(newSong, newTrack);
        if (newSongCover != null)
            setCover(newSong, newSongCover);
        if (newText != null)
            setLyrics(newSong, newText);

        FileUtility.appendRow(songsFilePath, songToRow(newSong));
    }

    @Override
    public void editSong(Song song, String newText, byte[] newTrack, byte[] newSongCover) throws BusinessException {

        if (newTrack != null)
            setTrack(song, newTrack);
        if (newSongCover != null)
            setCover(song, newSongCover);
        if (newText != null)
            setLyrics(song, newText);

        FileUtility.editRowById(songsFilePath, song.getId(), songToRow(song));

    }

    @Override
    public void deleteSong(Song song) throws BusinessException {

        final int ALBUM_SONG_COLUMN_INDEX = 5;
        final int PLAYLIST_SONG_COLUMN_INDEX = 3;

        FileUtility.deleteRowById(songsFilePath, song.getId()); // cancella riga canzone in canzoni.txt
        FileUtility.deleteFile(MUSIC_PATH + song.getId() + MUSIC_EXT); // cancella il file canzone
        FileUtility.deleteFile(LYRICS_PATH + song.getId() + LYRICS_EXT); // cancella il file dove è salvato il testo
        FileUtility.deleteFile(COVER_PATH + song.getId() + COVER_EXT); // cancella la foto della canzone

        // elimina la canzone dagli album e dalle playlist che la contengono
        FileUtility.deleteFromIndexColumnIfEqual(albumFilePath, ALBUM_SONG_COLUMN_INDEX, song.getId().toString());
        FileUtility.deleteFromIndexColumnIfEqual(playlistFilePath, PLAYLIST_SONG_COLUMN_INDEX, song.getId().toString());

    }

    @Override
    public String fetchLyrics(Song song) throws BusinessException {

        File file = new File(LYRICS_PATH + song.getId() + LYRICS_EXT);
        String result = "Testo non trovato";

        if (file.exists()) {

            try {
                byte[] data = Files.readAllBytes(file.toPath());
                result = new String(data, "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
                throw new BusinessException(e);
            }

        }
        return result;
    }

    @Override
    public byte[] fetchCover(Song song) throws BusinessException {

        byte[] cover = defaultCover;
        String coverPath = COVER_PATH + song.getId() + COVER_EXT;
        File f = new File(coverPath);
        if (f.exists()) {

            try {
                cover = Files.readAllBytes(f.toPath());
            } catch (IOException e) {
                e.printStackTrace();
                throw new BusinessException(e);
            }
        }
        return cover;
    }

    @Override
    public InputStream fetchTrack(Song song) throws BusinessException {

        String audioPath = MUSIC_PATH + song.getId() + MUSIC_EXT;
        File f = new File(audioPath);

        try {
            return new FileInputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new BusinessException(e);
        }
    }
    
    @Override
    public void setLyrics(Song song, String text) throws BusinessException {

        File f = new File(LYRICS_PATH + song.getId() + LYRICS_EXT);

        try {
            Files.write(f.toPath(), text.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(e);
        }
    }
    
    @Override
    public void setCover(Song song, byte[] cover) throws BusinessException {

        File f = new File(COVER_PATH + song.getId() + COVER_EXT);

        try {

            Files.write(f.toPath(), cover, StandardOpenOption.CREATE);

        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(e);
        }
    }

    @Override
    public void setTrack(Song song, byte[] audio) throws BusinessException {

        File f = new File(MUSIC_PATH + song.getId() + MUSIC_EXT);

        try {
            Files.write(f.toPath(), audio, StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(e);
        }
    }



    @Override
    public void removeArtistFromSong(Song song, Artist artist) throws BusinessException {
        song.getArtists().remove(artist);
        if (song.getArtists().isEmpty())
            deleteSong(song);

    }
}
