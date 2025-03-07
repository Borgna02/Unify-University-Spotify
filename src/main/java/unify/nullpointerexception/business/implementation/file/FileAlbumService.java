package unify.nullpointerexception.business.implementation.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import unify.nullpointerexception.business.AlbumService;
import unify.nullpointerexception.business.ArtistsService;
import unify.nullpointerexception.business.BusinessException;
import unify.nullpointerexception.business.GenreService;
import unify.nullpointerexception.business.MusicService;
import unify.nullpointerexception.domain.Album;
import unify.nullpointerexception.domain.Artist;
import unify.nullpointerexception.domain.Song;

public class FileAlbumService implements AlbumService {

    private MusicService musicService;
    private GenreService genreService;
    private ArtistsService artistsService;

    private String albumFilePath;
    private final String COVER_PATH = "src/main/resources/img/cover_album/";
    private final String COVER_EXT = ".png";
    private byte[] defaultCover;
    private int ID_INDEX = 0, NAME_INDEX = 1, ARTIST_ID_INDEX = 2, GENRE_ID_INDEX = 3, DATE_INDEX = 4,
            FIRST_SONG_INDEX = 5;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private int newId = -1;

    public FileAlbumService(String albumFilePath, MusicService musicService, GenreService genreService,
            ArtistsService artistsService) {

        this.musicService = musicService;
        this.genreService = genreService;
        this.artistsService = artistsService;
        this.albumFilePath = albumFilePath;

        // inizializzo la cover di default utilizzata qualora un album non ne abbia una
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
            FileData fileData = FileUtility.readAllRows(albumFilePath);
            int rowsNumber = fileData.getRows().size();
            newId = Integer.parseInt(fileData.getRows().get(rowsNumber - 1)[ID_INDEX]) + 1;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Album findAlbumById(Integer id) throws BusinessException {

        try {
            FileData fileData = FileUtility.readAllRows(albumFilePath);
            for (String[] columns : fileData.getRows()) {

                // id ; nome ; id_artista ; genere ; anno_uscita ; id_brano_1 ; ... ; id_brano_n
                if (Integer.parseInt(columns[ID_INDEX]) == id) {

                    Album album = new Album(id, genreService.findGenreById(Integer.parseInt(columns[GENRE_ID_INDEX])),
                            columns[NAME_INDEX], LocalDate.parse(columns[DATE_INDEX], formatter),
                            artistsService.findArtistById(Integer.parseInt(columns[ARTIST_ID_INDEX])));

                    // ciclo necessario perché le canzoni possono essere di numero variabile
                    for (int i = FIRST_SONG_INDEX; i < columns.length; i++) {
                        album.getSongs().add(musicService.findMusicById(Integer.parseInt(columns[i])));
                    }

                    return album;

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(e);
        }

        return null;

    }

    @Override
    public List<Album> findAllAlbumByArtist(Artist artist) throws BusinessException {

        List<Album> result = new ArrayList<>();

        try {
            FileData fileData = FileUtility.readAllRows(albumFilePath);
            for (String[] columns : fileData.getRows()) {

                // id ; nome ; id_artista ; genere ; id_brano_1 ; ... ; id_brano_n
                if (Integer.parseInt(columns[ARTIST_ID_INDEX]) == artist.getId()) {

                    Album album = new Album(Integer.parseInt(columns[ID_INDEX]),
                            genreService.findGenreById(Integer.parseInt(columns[GENRE_ID_INDEX])), columns[NAME_INDEX],
                            LocalDate.parse(columns[DATE_INDEX], formatter), artist);

                    for (int i = FIRST_SONG_INDEX; i < columns.length; i++) {
                        album.getSongs().add(musicService.findMusicById(Integer.parseInt(columns[i])));
                    }

                    result.add(album);

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(e);
        }

        return result;

    }

    @Override
    public List<Album> findAllAlbumByPrefix(String prefix) throws BusinessException {

        List<Album> result = new ArrayList<>();

        try {
            FileData fileData = FileUtility.readAllRows(albumFilePath);
            for (String[] columns : fileData.getRows()) {

                // id ; nome ; id_artista ; genere ; anno_uscita ; id_brano_1 ; ...; id_brano_n
                if (columns[NAME_INDEX].toLowerCase().contains(prefix.toLowerCase())) {

                    Album album = new Album(Integer.parseInt(columns[ID_INDEX]),
                            genreService.findGenreById(Integer.parseInt(columns[GENRE_ID_INDEX])), columns[NAME_INDEX],
                            LocalDate.parse(columns[DATE_INDEX], formatter),
                            artistsService.findArtistById(Integer.parseInt(columns[ARTIST_ID_INDEX])));

                    for (int i = FIRST_SONG_INDEX; i < columns.length; i++) {
                        album.getSongs().add(musicService.findMusicById(Integer.parseInt(columns[i])));
                    }
                    result.add(album);

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(e);
        }

        return result;
    }

    @Override
    public List<Album> findAllAlbumByGenrePrefix(String prefix) throws BusinessException {

        List<Album> result = new ArrayList<>();

        try {
            FileData fileData = FileUtility.readAllRows(albumFilePath);
            for (String[] columns : fileData.getRows()) {

                // id ; nome ; id_artista ; genere ; anno_uscita ; id_brano_1 ; ... ; id_brano_n
                if (genreService.findGenreById(Integer.parseInt(columns[GENRE_ID_INDEX])).getName().toLowerCase()
                        .contains(prefix.toLowerCase())) {

                    Album album = new Album(Integer.parseInt(columns[ID_INDEX]),
                            genreService.findGenreById(Integer.parseInt(columns[GENRE_ID_INDEX])), columns[NAME_INDEX],
                            LocalDate.parse(columns[DATE_INDEX], formatter),
                            artistsService.findArtistById(Integer.parseInt(columns[ARTIST_ID_INDEX])));

                    for (int i = FIRST_SONG_INDEX; i < columns.length; i++) {
                        album.getSongs().add(musicService.findMusicById(Integer.parseInt(columns[i])));
                    }

                    result.add(album);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(e);
        }

        return result;
    }

    @Override
    public List<Album> findAllAlbumBySong(Song song) throws BusinessException {

        List<Album> result = new ArrayList<>();

        try {
            FileData fileData = FileUtility.readAllRows(albumFilePath);
            for (String[] columns : fileData.getRows()) {

                // id ; nome ; id_artista ; genere ; anno_uscita ; id_brano_1 ; ... ; id_brano_n
                for (int i = FIRST_SONG_INDEX; i < columns.length; i++) {
                    if (Integer.parseInt(columns[i]) == song.getId()) {
                        result.add(findAlbumById(Integer.parseInt(columns[ID_INDEX])));
                        break;
                    }
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(e);
        }

        return result;
    }

    @Override
    public byte[] fetchCover(Album album) throws BusinessException {

        byte[] cover = defaultCover;
        String coverPath = COVER_PATH + album.getId() + COVER_EXT;
        File f = new File(coverPath);

        // se l'album ha una cover gli viene assegnata, altrimenti rimane quella di
        // default
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
    public void setCover(Album album, byte[] cover) throws BusinessException {

        File f = new File(COVER_PATH + album.getId() + COVER_EXT);

        try {
            Files.write(f.toPath(), cover, StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(e);
        }
    }

    // metodo necessario per convertire la playlist nel formato adatto al
    // salvataggio su file, ovvero:
    // id ; nome ; id_artista ; genere ; anno_uscita ; id_brano_1 ; ... ; id_brano_n

    private String albumToRow(Album album) {

        String row = album.getId() + FileUtility.COLUMN_SEPARATOR + album.getName() + FileUtility.COLUMN_SEPARATOR
                + album.getArtist().getId() + FileUtility.COLUMN_SEPARATOR + album.getGenre().getId()
                + FileUtility.COLUMN_SEPARATOR + album.getReleaseDate().format(formatter);

        for (Song song : album.getSongs())
            row += (FileUtility.COLUMN_SEPARATOR + song.getId());

        return row + '\n';
    }

    @Override
    public void createAlbum(Album newAlbum, byte[] newCover) throws BusinessException {

        newAlbum.setId(++newId);

        if (newCover != null)
            setCover(newAlbum, newCover);

        // aggiungo la nuova riga nel file
        FileUtility.appendRow(albumFilePath, albumToRow(newAlbum));

    }

    @Override
    public void editAlbum(Album album, byte[] cover) throws BusinessException {

        if (cover != null)
            setCover(album, cover);

        // modifico la riga dell'album nel file
        FileUtility.editRowById(albumFilePath, album.getId(), albumToRow(album));

    }

    @Override
    public void deleteAlbum(Album album) {

        FileUtility.deleteRowById(albumFilePath, album.getId());
        FileUtility.deleteFile(COVER_PATH + album.getId() + COVER_EXT);

    }

}