package net.viperfish.journal2;

import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.InputEvent;
import javafx.scene.web.HTMLEditor;
import javafx.util.Callback;
import net.viperfish.journal2.core.Journal;
import net.viperfish.journal2.transaction.JournalServices;

public final class JournalController implements Initializable {

	@FXML
	private ListView<Journal> journalList;

	@FXML
	private TextField searchText;

	@FXML
	private TextField keywordText;

	@FXML
	private HTMLEditor journalEditor;

	@FXML
	private Button saveBtn;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		journalList.setCellFactory(new Callback<ListView<Journal>, ListCell<Journal>>() {

			@Override
			public ListCell<Journal> call(ListView<Journal> param) {
				return new ListCell<Journal>() {

					@Override
					protected void updateItem(Journal item, boolean empty) {
						System.out.println(item);
						super.updateItem(item, empty);
						if (item != null) {
							setText(item.getTimestamp().toString());
						}
					}

				};
			}
		});

		journalList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Journal>() {

			@Override
			public void changed(ObservableValue<? extends Journal> observable, Journal oldValue, Journal newValue) {
				keywordText.setText(newValue.getSubject());
				journalEditor.setHtmlText(newValue.getContent());
			}
		});

		showAll();

		journalEditor.addEventHandler(InputEvent.ANY, new EventHandler<InputEvent>() {

			@Override
			public void handle(InputEvent arg0) {
				saveBtn.setDisable(false);
			}

		});
	}

	@FXML
	public void newJournal(ActionEvent e) {
		Service<Journal> addEmpty = JournalServices.newSaveJournalService(new Journal());
		addEmpty.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent event) {
				refreshList();
				journalList.getSelectionModel().select(0);
			}
		});
		addEmpty.start();
	}

	@FXML
	public void saveJournal(ActionEvent e) {
		Journal selected = journalList.getSelectionModel().getSelectedItem();
		selected.setSubject(keywordText.getText());
		selected.setContent(journalEditor.getHtmlText());
		Service<Journal> save = JournalServices.newSaveJournalService(selected);
		save.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent event) {
				refreshList();

			}
		});
		save.start();
		saveBtn.setDisable(true);
	}

	@FXML
	public void deleteJournal(ActionEvent e) {
		Journal selected = journalList.getSelectionModel().getSelectedItem();
		long id = selected.getId();

		Service<Journal> delete = JournalServices.newRemoveJournalService(id);
		delete.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent event) {
				refreshList();
			}
		});
		delete.start();
	}

	private void refreshList() {
		if (searchText.getText().trim().isEmpty()) {
			showAll();
		}
	}

	private void showAll() {
		Service<Collection<Journal>> getAll = JournalServices.newGetAllService();
		getAll.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent event) {
				Collection<Journal> result = (Collection<Journal>) event.getSource().getValue();
				System.out.println("Result:" + result);
				ObservableList<Journal> toView = FXCollections.observableArrayList(result);
				journalList.setItems(toView);
			}
		});
		getAll.setOnFailed(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent event) {
				Alert error = new Alert(AlertType.ERROR, "Cannot get all entries");
				error.showAndWait();
			}
		});
		getAll.start();
	}

}
