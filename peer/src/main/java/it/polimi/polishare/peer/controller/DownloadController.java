package it.polimi.polishare.peer.controller;

import com.jfoenix.controls.JFXProgressBar;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import it.polimi.polishare.peer.utils.Download;
import it.polimi.polishare.peer.utils.DownloadManager;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;

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
        refreshData();
    }

    public void updateDownload(String title, Download download) {
        SingleDownloadUI downloadUI = downloadBoxes.get(title);
        if(download.getReceivedQuantity() < 1.0)
            downloadUI.progress(download.getReceivedQuantity());
        else
            downloadUI.completed();
    }

    private void refreshData() {
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
        private VBox root;
        private HBox header;
        private Label titleLabel;
        private Label percentLabel;

        private JFXProgressBar progressBar;

        public SingleDownloadUI(String title, Download downloadData) {
            this.root = new VBox();
            this.header = new HBox();
            this.progressBar = new JFXProgressBar();
            this.titleLabel = new Label(title);
            this.percentLabel = new Label(String.format( "%.2f", downloadData.getReceivedQuantity() * 100) + "%");

            GridPane.setHgrow(root, Priority.ALWAYS);
            GridPane.setMargin(root, new Insets(20.0));
            VBox.setMargin(root, new Insets(20.0));
            VBox.setVgrow(header, Priority.ALWAYS);
            VBox.setVgrow(progressBar, Priority.ALWAYS);


            root.setSpacing(20.0);

            titleLabel.setStyle("-fx-font-size: 20");
            percentLabel.setStyle("-fx-font-size: 20");
            Region headerRegion = new Region();
            HBox.setHgrow(headerRegion, Priority.ALWAYS);
            header.setAlignment(Pos.CENTER);
            header.getChildren().addAll(titleLabel, headerRegion, percentLabel);

            Separator separator = new Separator();
            separator.setOrientation(Orientation.HORIZONTAL);

            progressBar.setProgress(downloadData.getReceivedQuantity());
            VBox.setVgrow(progressBar, Priority.ALWAYS);
            progressBar.getStyleClass().add("custom-jfx-progress-bar-stroke");

            root.getChildren().addAll(header, progressBar, separator);
        }

        public void progress(double progress) {
            this.percentLabel.setText(String.format( "%.2f", progress * 100) + "%");
            progressBar.setProgress(progress);
        }

        public void completed() {
            this.percentLabel.setText("Completato");
            progressBar.setProgress(1.0);
            progressBar.getStyleClass().clear();
            progressBar.setStyle("-fx-background-color: rgb(102.0, 153.0, 102.0); -fx-padding: 6; -fx-pref-width: 500;");
        }

        public VBox getRoot() {
            return root;
        }
    }
}
