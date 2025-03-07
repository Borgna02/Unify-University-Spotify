package unify.nullpointerexception.controller;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import unify.nullpointerexception.business.BusinessException;
import unify.nullpointerexception.business.MusicService;
import unify.nullpointerexception.business.UnifyBusinessFactory;
import unify.nullpointerexception.domain.Song;
import unify.nullpointerexception.domain.Artist;
import unify.nullpointerexception.view.ViewDispatcher;

public class MusicBarController implements Initializable, DataInitializable<Song> {

    private final String PAUSE_IMAGE_PATH = "src/main/resources/img/pause.png";

    private final String PLAY_IMAGE_PATH = "src/main/resources/img/play.png";

    private Clip controller;
    private AudioInputStream stream;
    private long tempoAttuale;
    private int queueIndex;

    private boolean isLoaded;
    private volatile Boolean isPlaying = false;

    private Song song;
    private List<Song> songsQueue = new ArrayList<>();

    private float actualVolume = 50f;

    @FXML
    private VBox audioPlayerMsgBox;

    @FXML
    private Text whatIsPlayingMsg;

    @FXML
    private Text playASongMsg;

    @FXML
    private Text songTitle;

    @FXML
    private Text artistName;

    @FXML
    private ImageView playPauseButton;

    @FXML
    private Slider songSlider;

    @FXML
    private Text songActualTime;

    @FXML
    private Text songTotalTime;

    @FXML
    private Slider volumeSlider;

    @FXML
    private ImageView skipBack;

    @FXML
    private ImageView skipFwd;

    private ViewDispatcher dispatcher;
    private MusicService musicService;

    // costruttore
    public MusicBarController() {
        dispatcher = ViewDispatcher.getInstance();
        UnifyBusinessFactory factory = UnifyBusinessFactory.getInstance();
        musicService = factory.getMusicService();
    }

    // inizializzo il controller
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        audioPlayerMsgBox.getChildren().removeAll(whatIsPlayingMsg, songTitle, artistName);
        volumeSlider.setValue(actualVolume);

        sliderDrag();
        volumeSliderDrag();

        playPauseButton.setDisable(true);
        playPauseButton.setOpacity(0.3);

        skipBack.setDisable(true);
        skipBack.setOpacity(0.3);

        skipFwd.setDisable(true);
        skipFwd.setOpacity(0.3);

        // timeUpdater = new TimeUpdater(songActualTime, isPlaying,
        // controller.getMicrosecondPosition());
        // sliderUpdater = new SliderUpdater(songSlider, isPlaying,
        // controller.getMicrosecondPosition(), controller.getMicrosecondLength());
    }

    // metodo chiamato quando si trascina il pallino dello slider dell'avanzamento
    // della canzone
    public void sliderDrag() {
        songSlider.valueProperty().addListener(new InvalidationListener() {
            boolean pressed = false;
            long newTempoAttuale = 0;

            public void invalidated(Observable ov) {
                // le seguenti righe servono ad effettuare il jump solo al termine dello
                // spostamento dello slider e non durante, in modo da non creare suoni
                // spiacevoli

                if (songSlider.isPressed()) {
                    newTempoAttuale = (long) (controller.getMicrosecondLength() *
                            (songSlider.getValue() / 10000));
                    pressed = true;
                }
                if (pressed && !songSlider.isPressed()) {
                    pressed = false;
                    jump(newTempoAttuale);

                }
            }
        });
    }

    // metodo chiamato quando si trascina il pallino dello slider del volume
    public void volumeSliderDrag() {
        volumeSlider.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                if (volumeSlider.isPressed()) {
                    actualVolume = (float) volumeSlider.getValue();
                    setVolume(actualVolume);
                }
            }
        });
    }

    // metodo chiamato quando si preme play/pausa
    @FXML
    public void playPressed(MouseEvent event) {

        if (isPlaying) {
            pause();
        } else {
            play();
        }
    }

    // metodo chiamato quando si preme il tasto per andare alla canzone successiva
    @FXML
    public void skipFwdPressed(MouseEvent event) {

        loadNextInQueue();
        play();
    }

    // metodo chiamato quando si preme il tasto per andare alla canzone precedente
    @FXML
    public void skipBackPressed(MouseEvent event) {

        // se il tasto viene premuto entro 2 secondi dall'inizo della riproduzione,
        // allora viene caricata la canzone precedente, altrimenti viene ricaricata da
        // capo quella attuale
        if (controller.getMicrosecondPosition() < 2000000) {
            loadPreviousInQueue();
        } else {
            loadSong(song, actualVolume);
        }

        // riproduco la canzone
        play();

    }

    // metodo chiamato quando si clicca sul box blu contenente il titolo della
    // canzone
    @FXML
    public void showSongView() {
        if (isLoaded) {
            try {
                dispatcher.renderView("song", song);
            } catch (IOException | BusinessException e) {
                e.printStackTrace();
            }
        }
    }

    // metodo per caricare una canzone nel player
    public void loadSong(Song song, float volume) {

        stop();
        try {

            controller = AudioSystem.getClip();
            controller.addLineListener(event -> {

                if (event.getType() == LineEvent.Type.STOP) {
                    long tot = controller.getMicrosecondLength();
                    long curr = controller.getMicrosecondPosition();

                    if (curr == tot) {
                        stop();
                        loadNextInQueue();
                    }
                }
            });

            InputStream is = musicService.fetchTrack(song); // carico uno stream generico (può venire da qualsiasi
                                                            // sorgente: file, rete, ram etc.)
            stream = AudioSystem.getAudioInputStream(new BufferedInputStream(is));

            controller.open(stream);

        } catch (Exception e) {
            e.printStackTrace();
        }

        isLoaded = true;

        if (volume != -1)
            this.actualVolume = volume;
        setVolume(this.actualVolume);

        this.song = song;
        songTitle.setText(song.getTitle());

        /* Blocco artisti canzone */
        String artistList = new String();
        int maxArtistsList = 3;
        for (Artist a : song.getArtists()) {
            if (maxArtistsList > 0) {
                artistList = artistList + a.getStageName() + "\n";
                maxArtistsList--;
            } else {
                artistList = artistList + "...\n";
            }

        }

        artistName.setText(artistList);
        songTotalTime.setText(getTimeStamp(controller.getMicrosecondLength()));

        if (audioPlayerMsgBox.getChildren().size() == 1)
            audioPlayerMsgBox.getChildren().addAll(whatIsPlayingMsg, songTitle, artistName);

        audioPlayerMsgBox.getChildren().remove(playASongMsg);

        playPauseButton.setDisable(false);
        playPauseButton.setOpacity(1);
        skipBack.setDisable(false);
        skipBack.setOpacity(1);
        skipFwd.setDisable(false);
        skipFwd.setOpacity(1);
        play();
    }

    // metodo per chiudere il player
    public void stop() {

        if (!isLoaded)
            return;

        pause();
        tempoAttuale = 0L;
        controller.close();
    }

    // gestione della coda
    public void loadQueue(List<Song> queue, float volume) {
        songsQueue = queue;
        loadSong(queue.get(0), volume);
        queueIndex = 1;

    }

    public void loadQueue(Song c, float volume) {
        songsQueue.clear();
        songsQueue.add(c);
        loadSong(c, volume);
        queueIndex = 1;
    }

    public void addToQueue(List<Song> queue) {
        this.songsQueue.addAll(queue);
        if (!isLoaded) {
            loadSong(songsQueue.get(0), -1);
            queueIndex = 1;
        }
    }

    public void addToQueue(Song song) {
        this.songsQueue.add(song);
        if (!isLoaded) {
            loadSong(song, -1);
            queueIndex = 1;
        }
    }

    private void loadNextInQueue() {
        loadSong(songsQueue.get(Math.floorMod(queueIndex++, songsQueue.size())), actualVolume);
    }

    private void loadPreviousInQueue() {
        loadSong(songsQueue.get(Math.floorMod(queueIndex--, songsQueue.size())), actualVolume);
    }

    public void clearQueue() {

        if (isLoaded) {
            if (isPlaying) {
                stop();
            }
            songsQueue.clear();
            song = null;
        }

    }
    // fine gestione della coda

    // gestione dell'audio
    private void togglePlayButton() {
        try {

            if (isPlaying) {

                playPauseButton.setImage(new Image(new FileInputStream(PAUSE_IMAGE_PATH)));

                // thread che aggiorna lo slider durante la riproduzione dell'audio

                new Thread(() -> {

                    while (isPlaying) { // il thread si ferma quando la canzone viene messa in pausa

                        Platform.runLater(() -> {
                            songActualTime.setText(getTimeStamp(controller.getMicrosecondPosition()));
                        });

                        try {
                            Thread.sleep(50); // pausa di 50 millisecondi
                        } catch (InterruptedException e) {

                            e.printStackTrace();
                        }
                    }

                }).start();

                // thread che aggiorna il tempo della canzone in riproduzione

                new Thread(() -> {

                    while (isPlaying) { // il thread si ferma quando la canzone viene
                                        // messa in pausa
                        if (!songSlider.isPressed()) {
                            // converto i microsecondi in una scala da 0 a 10000

                            Platform.runLater(() -> {
                                long newpos = controller.getMicrosecondPosition() * 10000L
                                        / controller.getMicrosecondLength();
                                songSlider.setValue(newpos);
                            });
                            // imposto il nuovo valore calcolato
                        }

                        try {
                            Thread.sleep(5); // pausa per 5 millisecondi
                        } catch (InterruptedException e) {

                            e.printStackTrace();
                        }
                    }

                }).start();

            } else {
                playPauseButton.setImage(new Image(new FileInputStream(PLAY_IMAGE_PATH)));

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void play() {

        if (!isLoaded)
            return;
        isPlaying = true;

        togglePlayButton();

        controller.setMicrosecondPosition(tempoAttuale);
        controller.start();
    }

    private void pause() {

        if (!isLoaded)
            return;

        isPlaying = false;

        togglePlayButton();

        tempoAttuale = controller.getMicrosecondPosition();
        controller.stop();

    }

    private void jump(long microsec) {

        if (!isLoaded)
            return;

        controller.stop();

        if (microsec >= 0 && microsec < controller.getMicrosecondLength()) {
            tempoAttuale = microsec;
        }

        // per non far partire la canzone se lo slider viene spostato a canzone in pausa
        if (isPlaying)
            play();
    }

    private String getTimeStamp(Long tempoOriginale) {

        if (!isLoaded)
            return "--:--";

        long secondi = tempoOriginale / 1000000L;

        long minuti = secondi / 60L;
        long secondiAvanzati = secondi % 60L;
        String tempo = "";

        tempo += Long.toString(minuti) + ":";

        if (secondiAvanzati < 10L)
            tempo += "0";

        tempo += Long.toString(secondiAvanzati);

        return tempo;
    }

    private void setVolume(float value) { // valore da 0 a 100

        if (!isLoaded) {
            actualVolume = value;
            return;
        }

        value /= 100f;
        FloatControl volControl = (FloatControl) controller.getControl(FloatControl.Type.MASTER_GAIN);

        float dB = ((float) (Math.log(value) / Math.log(10.0) * 20.0)) * 2;

        if (dB < -80.0f)
            dB = -80.0f;
        volControl.setValue(dB);
    }
    // fine gestione dell'audio
}
