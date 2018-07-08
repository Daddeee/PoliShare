package it.polimi.polishare.peer.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.svg.SVGGlyph;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import it.polimi.polishare.peer.network.download.Download;
import it.polimi.polishare.peer.network.download.DownloadManager;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@ViewController(value = "/view/Download.fxml", title = "Polishare")
public class DownloadController {
    @FXMLViewFlowContext
    private ViewFlowContext context;

    @FXML
    private GridPane downloadBox;

    private HashMap<String, SingleDownloadUI> downloadBoxes;
    private HashMap<String, Download> activeDownloads;

    @PostConstruct
    public void initData() {
        DownloadManager.setDownloadController(this);
        initDownloadData();
    }

    @FXML
    public void startButtonAction(SingleDownloadUI singleDownloadUI) {
        singleDownloadUI.getDownload().start();

        singleDownloadUI.getStartButton().setDisable(true);
        singleDownloadUI.getStopButton().setDisable(false);
        singleDownloadUI.getRemoveButton().setDisable(true);
    }

    @FXML
    public void stopButtonAction(SingleDownloadUI singleDownloadUI) {
        singleDownloadUI.getDownload().stop();
        singleDownloadUI.stopped();

        singleDownloadUI.getStartButton().setDisable(true);
        singleDownloadUI.getStopButton().setDisable(true);
        singleDownloadUI.getRemoveButton().setDisable(false);
    }

    @FXML
    public void removeButtonAction(SingleDownloadUI singleDownloadUI) {
        downloadBox.getChildren().remove(singleDownloadUI.getRoot());
        DownloadManager.removeActiveDownload(singleDownloadUI.getDownload().getNote().getTitle());
    }

    public void updateDownload(String title, Download download) {
        SingleDownloadUI downloadUI = downloadBoxes.get(title);
        if (download.getReceivedPercentage() < 1.0) {
            downloadUI.progress(download.getReceivedPercentage());
        } else {
            downloadUI.completed();
            downloadUI.getStartButton().setDisable(true);
            downloadUI.getStopButton().setDisable(true);
            downloadUI.getRemoveButton().setDisable(false);
        }
    }

    private void initDownloadData() {
        activeDownloads = DownloadManager.getActiveDownloads();
        downloadBoxes = new HashMap<>();

        downloadBox.getChildren().clear();
        downloadBoxes.clear();
        int i = 0;
        for(Map.Entry<String, Download> e : activeDownloads.entrySet()) {
            SingleDownloadUI downloadUI = new SingleDownloadUI(e.getKey(), e.getValue());
            downloadBoxes.put(e.getKey(), downloadUI);
            downloadBox.add(downloadUI.getRoot(), 0, i);
            i++;
        }
    }

    private class SingleDownloadUI {
        private Download downloadData;
        private VBox root;
        private HBox header;
        private HBox body;
        private Label titleLabel;
        private Label percentLabel;
        private JFXButton startButton;
        private JFXButton stopButton;
        private JFXButton removeButton;
        private JFXProgressBar progressBar;

        public SingleDownloadUI(String title, Download downloadData) {
            this.downloadData = downloadData;
            this.root = new VBox();
            this.header = new HBox();
            this.body = new HBox();
            this.startButton = new JFXButton();
            this.stopButton = new JFXButton();
            this.removeButton = new JFXButton();
            this.progressBar = new JFXProgressBar();
            this.titleLabel = new Label(title);
            this.percentLabel = new Label(String.format( "%.2f", downloadData.getReceivedPercentage() * 100) + "%");

            GridPane.setHgrow(root, Priority.ALWAYS);
            GridPane.setMargin(root, new Insets(20.0));
            VBox.setMargin(root, new Insets(20.0));
            VBox.setVgrow(header, Priority.ALWAYS);
            VBox.setVgrow(body, Priority.ALWAYS);


            root.setSpacing(20.0);

            titleLabel.setStyle("-fx-font-size: 20");
            percentLabel.setStyle("-fx-font-size: 20");
            Region headerRegion = new Region();
            HBox.setHgrow(headerRegion, Priority.ALWAYS);
            header.setAlignment(Pos.CENTER);
            header.getChildren().addAll(titleLabel, headerRegion,percentLabel);

            Separator separator = new Separator();
            separator.setOrientation(Orientation.HORIZONTAL);


            SVGGlyph startGlyph = new SVGGlyph("M424.4 214.7L72.4 6.6C43.8-10.3 0 6.1 0 47.9V464c0 37.5 40.7 60.1 72.4 41.3l352-208c31.4-18.5 31.5-64.1 0-82.6z");
            startGlyph.setPrefSize(20.0, 20.0);
            startGlyph.setStyle("-fx-background-color: #5264AE");
            startButton.setGraphic(startGlyph);
            startButton.setStyle("-fx-background-color: rgba(0.0, 0.0, 0.0, 0.0); ");
            startButton.setButtonType(JFXButton.ButtonType.FLAT);
            startButton.setRipplerFill(Color.LIGHTBLUE);
            startButton.setDisable(downloadData.isStarted());
            startButton.setOnAction(event -> startButtonAction(this));

            SVGGlyph stopGlyph = new SVGGlyph("M400 32H48C21.5 32 0 53.5 0 80v352c0 26.5 21.5 48 48 48h352c26.5 0 48-21.5 48-48V80c0-26.5-21.5-48-48-48z");
            stopGlyph.setPrefSize(20.0, 20.0);
            stopGlyph.setStyle("-fx-background-color: #5264AE");
            stopButton.setGraphic(stopGlyph);
            stopButton.setStyle("-fx-background-color: rgba(0.0, 0.0, 0.0, 0.0);");
            stopButton.setButtonType(JFXButton.ButtonType.FLAT);
            stopButton.setRipplerFill(Color.LIGHTBLUE);
            stopButton.setDisable(!downloadData.isStarted());
            stopButton.setOnAction(event -> stopButtonAction(this));

            SVGGlyph removeGlyph = new SVGGlyph("M242.72 256l100.07-100.07c12.28-12.28 12.28-32.19 0-44.48l-22.24-22.24c-12.28-12.28-32.19-12.28-44.48 0L176 189.28 75.93 89.21c-12.28-12.28-32.19-12.28-44.48 0L9.21 111.45c-12.28 12.28-12.28 32.19 0 44.48L109.28 256 9.21 356.07c-12.28 12.28-12.28 32.19 0 44.48l22.24 22.24c12.28 12.28 32.2 12.28 44.48 0L176 322.72l100.07 100.07c12.28 12.28 32.2 12.28 44.48 0l22.24-22.24c12.28-12.28 12.28-32.19 0-44.48L242.72 256z");
            removeGlyph.setPrefSize(20.0, 20.0);
            removeGlyph.setStyle("-fx-background-color: #5264AE");
            removeButton.setGraphic(removeGlyph);
            removeButton.setStyle("-fx-background-color: rgba(0.0, 0.0, 0.0, 0.0);");
            removeButton.setButtonType(JFXButton.ButtonType.FLAT);
            removeButton.setRipplerFill(Color.LIGHTBLUE);
            removeButton.setDisable(downloadData.isStarted());
            removeButton.setOnAction(event -> removeButtonAction(this));

            Region bodyRegion = new Region();
            HBox.setHgrow(bodyRegion, Priority.ALWAYS);

            progressBar.setProgress(downloadData.getReceivedPercentage());
            HBox.setHgrow(progressBar, Priority.ALWAYS);
            progressBar.getStyleClass().add("custom-jfx-progress-bar-stroke");


            body.getChildren().addAll(startButton, stopButton, removeButton, bodyRegion, progressBar);
            body.setSpacing(10.0);
            body.setAlignment(Pos.CENTER);

            root.getChildren().addAll(header, body, separator);
        }

        public Download getDownload() {
            return downloadData;
        }

        public void progress(double progress) {
            if(!this.percentLabel.getText().equals("Annullato"))
                this.percentLabel.setText(String.format( "%.2f", progress * 100) + "%");
            progressBar.setProgress(progress);
        }

        public void completed() {
            this.percentLabel.setText("Completato");
            progressBar.setProgress(1.0);
            progressBar.getStyleClass().clear();
            progressBar.setStyle("-fx-background-color: rgb(102.0, 153.0, 102.0); -fx-padding: 6; -fx-pref-width: 500;");
        }

        public void stopped() {
            this.percentLabel.setText("Annullato");
            progressBar.getStyleClass().clear();
            progressBar.setStyle("-fx-background-color: rgba(244.0, 66.0, 66.0, 1.0); -fx-padding: 6; -fx-pref-width: 500;");

        }

        public VBox getRoot() {
            return root;
        }

        public JFXButton getStartButton() {
            return startButton;
        }

        public JFXButton getStopButton() {
            return stopButton;
        }

        public JFXButton getRemoveButton() {
            return removeButton;
        }
    }
}
