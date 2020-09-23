package com.bas.component;

import com.bas.enumerable.ModalType;
import com.bas.model.Student;
import com.bas.service.ExcelService;
import com.bas.service.StudentService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Components {

    private final StudentService studentService = StudentService.getInstance();

    private final ExcelService excelService = ExcelService.getInstance();

    public void initPrincipal(Stage parentStage) {

        final ObservableList<Student>[] data = new ObservableList[]{FXCollections.observableArrayList(studentService.getAllStudents())};

        TableView<Student> table = new TableView<>();
        table.setMinHeight(300);
        table.setMaxHeight(300);
        table.setMinWidth(420);
        table.setMaxWidth(420);

        final Stage[] completeExcelModal = {getIdCompleteModal(parentStage)};
        final Stage[] newStudentModal = {getNewStudentModal(parentStage, ModalType.NEW, new Student())};

        TextField filterInput = new TextField ();
        filterInput.setMaxWidth(200);
        filterInput.setMinWidth(200);
        filterInput.textProperty().addListener((observable, oldValue, newValue) -> {
            data[0].removeAll(data[0]);
            data[0].addAll(FXCollections.observableArrayList(studentService.getFilteredStudents(newValue)));
        });

        Button newStudentButton = new Button();
        newStudentButton.setText("Agregar alumno");
        AnchorPane.setLeftAnchor(newStudentButton, 0d);
        AnchorPane.setRightAnchor(newStudentButton, 0d);

        Button deleteStudentButton = new Button();
        deleteStudentButton.setText("Dar de baja");
        AnchorPane.setLeftAnchor(deleteStudentButton, 0d);
        AnchorPane.setRightAnchor(deleteStudentButton, 0d);

        Button editStudentButton = new Button();
        editStudentButton.setText("Editar alumno");
        AnchorPane.setLeftAnchor(editStudentButton, 0d);
        AnchorPane.setRightAnchor(editStudentButton, 0d);

        Button completeExcelButton = new Button();
        completeExcelButton.setText("Cargar números de legajo");
        completeExcelButton.setMinHeight(40);
        completeExcelButton.setFont(new Font("Arial", 15));
        AnchorPane.setLeftAnchor(completeExcelButton, 0d);
        AnchorPane.setRightAnchor(completeExcelButton, 0d);

        AnchorPane anchorPaneNewStudent = new AnchorPane();
        anchorPaneNewStudent.getChildren().add(newStudentButton);

        AnchorPane anchorPaneDeleteStudent = new AnchorPane();
        anchorPaneDeleteStudent.getChildren().add(deleteStudentButton);

        AnchorPane anchorPaneEditStudent = new AnchorPane();
        anchorPaneEditStudent.getChildren().add(editStudentButton);

        AnchorPane anchorPaneCompleteExcel = new AnchorPane();
        anchorPaneCompleteExcel.getChildren().add(completeExcelButton);

        Label title1 = new Label("Alumnos");
        title1.setFont(new Font("Arial", 20));

        Label title2 = new Label("Completar planilla de Excel");
        title2.setFont(new Font("Arial", 20));

        Label filterLabel = new Label("Buscar:");
        filterLabel.setFont(new Font("Arial", 14));
        filterLabel.setPadding(new Insets(4, 0, 0, 0));

        TableColumn<Student, String> idColumn = new TableColumn<>("N° Legajo");
        idColumn.setMinWidth(70);
        idColumn.setMaxWidth(70);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setStyle( "-fx-alignment: CENTER;");

        TableColumn<Student, String> firstNameColumn = new TableColumn<>("Nombre");
        firstNameColumn.setMinWidth(167);
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Student, String> lastNameColumn = new TableColumn<>("Apellido");
        lastNameColumn.setMinWidth(167);
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        table.setItems(data[0]);
        table.getColumns().addAll(idColumn, lastNameColumn, firstNameColumn);

        HBox filterHBox = new HBox();
        filterHBox.setSpacing(10);
        filterHBox.getChildren().addAll(filterLabel, filterInput);
        HBox title2HBox = new HBox();
        title2HBox.getChildren().add(title2);
        title2HBox.setMargin(title2, new Insets(10, 0, 0, 0));
        HBox tableButtonsHBox = new HBox();
        tableButtonsHBox.setSpacing(10);
        VBox buttonsVBox = new VBox();
        buttonsVBox.setSpacing(10);
        buttonsVBox.setMaxWidth(150);
        buttonsVBox.setMinWidth(150);
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10));
        buttonsVBox.getChildren().addAll(anchorPaneNewStudent, anchorPaneEditStudent, anchorPaneDeleteStudent);
        tableButtonsHBox.getChildren().addAll(table, buttonsVBox);
        vBox.getChildren().addAll(title1, filterHBox, tableButtonsHBox, title2HBox, anchorPaneCompleteExcel);

        StackPane stackPane = new StackPane();
        parentStage.setTitle("Buenos Aires School");
        parentStage.setScene(new Scene(stackPane, 600, 500));
        stackPane.getChildren().add(vBox);
        parentStage.setResizable(false);
        parentStage.show();

        newStudentButton.setOnAction(event -> {
            newStudentModal[0] = getNewStudentModal(parentStage, ModalType.NEW, new Student());
            newStudentModal[0].setOnHiding(newEvent -> {
                data[0].removeAll(data[0]);
                data[0].addAll(FXCollections.observableArrayList(studentService.getAllStudents()));
            });
            newStudentModal[0].show();
        });

        editStudentButton.setOnAction(event -> {
            if (table.getSelectionModel().getSelectedIndex() > -1) {
                Student student = table.getSelectionModel().getSelectedItem();
                newStudentModal[0] = getNewStudentModal(parentStage, ModalType.EDIT, student);
                newStudentModal[0].setOnHiding(newEvent -> {
                    data[0].removeAll(data[0]);
                    data[0].addAll(FXCollections.observableArrayList(studentService.getAllStudents()));
                });
                newStudentModal[0].show();
            }
        });

        deleteStudentButton.setOnAction(event -> {
            if (table.getSelectionModel().getSelectedIndex() > -1) {
                Student student = table.getSelectionModel().getSelectedItem();
                Optional<ButtonType> status = getConfirmationDialog("Eliminar alumno", "¿Desea eliminar del listado al siguiente alumno?\n" +
                                                                                                "\nNúmero de legajo: " + student.getId() +
                                                                                                "\nApellido: " + student.getLastName() +
                                                                                                "\nNombre: " + student.getFirstName() + "\n ").showAndWait();
                if(ButtonType.OK.equals(status.get())) {
                    studentService.deleteStudent(student);
                    data[0].removeAll(data[0]);
                    data[0].addAll(FXCollections.observableArrayList(studentService.getAllStudents()));
                }
            }
        });

        completeExcelButton.setOnAction(event -> {
            completeExcelModal[0] = getIdCompleteModal(parentStage);
            completeExcelModal[0].show();
        });
    }

    public Stage getNewStudentModal(Stage parentStage, ModalType type, Student student) {
        StackPane stackPane = new StackPane();

        TableView table = new TableView();
        table.setMaxHeight(100);
        table.setMinHeight(100);

        ObservableList<String> data = FXCollections.observableArrayList(student.getParentsCuil());

        TableColumn<String, String> cuilResponsablesColumn = new TableColumn<>("CUIL");
        cuilResponsablesColumn.prefWidthProperty().bind(table.widthProperty().divide(1.02));
        table.getColumns().add(cuilResponsablesColumn);
        cuilResponsablesColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()));
        table.setItems(data);

        VBox vBox = new VBox();
        HBox hBoxTableAndActions = new HBox();
        hBoxTableAndActions.setSpacing(10);
        VBox vBoxTableActions = new VBox();
        vBoxTableActions.setSpacing(10);
        vBoxTableActions.setMaxWidth(100);
        vBoxTableActions.setMinWidth(100);
        TextField firstNameInput = new TextField ();
        firstNameInput.setText(student.getFirstName());
        TextField lastNameInput = new TextField ();
        lastNameInput.setText(student.getLastName());
        TextField idInput = new TextField ();
        idInput.setText(student.getId());
        idInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                idInput.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        if(ModalType.EDIT.equals(type)) {
            idInput.setDisable(true);
        }

        Label idLabel = new Label("Número de legajo");
        Label lastNameLabel = new Label("Apellido");
        Label firstNameLabel = new Label("Nombre");
        Label cuilLabel = new Label("CUIL responsable de pago");

        Button cancelButton = new Button();
        cancelButton.setText("Cancelar");
        AnchorPane.setLeftAnchor(cancelButton, 0d);
        AnchorPane.setRightAnchor(cancelButton, 0d);

        Button acceptButton = new Button();
        acceptButton.setText("Guardar");
        acceptButton.setDisable(student.getId().isEmpty());
        AnchorPane.setLeftAnchor(acceptButton, 0d);
        AnchorPane.setRightAnchor(acceptButton, 0d);

        Button newParentButton = new Button();
        newParentButton.setText("Agregar");
        AnchorPane.setLeftAnchor(newParentButton, 0d);
        AnchorPane.setRightAnchor(newParentButton, 0d);

        Button deleteParentButton = new Button();
        deleteParentButton.setText("Eliminar");
        AnchorPane.setLeftAnchor(deleteParentButton, 0d);
        AnchorPane.setRightAnchor(deleteParentButton, 0d);

        AnchorPane anchorPaneCancel = new AnchorPane();
        anchorPaneCancel.getChildren().add(cancelButton);

        AnchorPane anchorPaneAccept = new AnchorPane();
        anchorPaneAccept.getChildren().add(acceptButton);

        AnchorPane anchorPaneNewParent = new AnchorPane();
        anchorPaneNewParent.getChildren().add(newParentButton);

        AnchorPane anchorPaneDeleteParent = new AnchorPane();
        anchorPaneDeleteParent.getChildren().add(deleteParentButton);

        vBoxTableActions.getChildren().addAll(anchorPaneNewParent, anchorPaneDeleteParent);
        hBoxTableAndActions.getChildren().addAll(table, vBoxTableActions);
        vBox.getChildren().addAll(idLabel, idInput, lastNameLabel, lastNameInput, firstNameLabel, firstNameInput, cuilLabel, hBoxTableAndActions, anchorPaneAccept, anchorPaneCancel);
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10));
        stackPane.getChildren().add(vBox);

        idInput.textProperty().addListener((observable, oldValue, newValue) -> {
            acceptButton.setDisable(newValue.isEmpty());
        });

        Stage stage = new Stage();
        if(ModalType.NEW.equals(type)) {
            stage.setTitle("Nuevo alumno");
        } else {
            stage.setTitle("Editar alumno");
        }
        stage.setScene(new Scene(stackPane, 350, 430));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(parentStage);
        stage.setResizable(false);
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Optional<ButtonType> status = getConfirmationDialog("Atención", "Si cierra la ventana, se perderán los datos que no haya guardado. ¿Desea cerrar la ventana de todas formas?").showAndWait();
                if(!ButtonType.OK.equals(status.get())) {
                    event.consume();
                }
            }
        });

        final Stage[] cuilModal = {getNewCuilModal(stage, student.getParentsCuil())};

        cancelButton.setOnAction(event -> {
            Optional<ButtonType> status = getConfirmationDialog("Atención", "Si cierra la ventana, se perderán los datos que no haya guardado. ¿Desea cerrar la ventana de todas formas?").showAndWait();
            if(ButtonType.OK.equals(status.get())) {
                stage.close();
            }
        });

        acceptButton.setOnAction(event -> {
            student.setId(idInput.getText());
            student.setFirstName(firstNameInput.getText());
            student.setLastName(lastNameInput.getText());
            if(ModalType.NEW.equals(type)) {
                Student newStudent = studentService.addStudent(student);
                if(newStudent == null) {
                    getErrorDialog("Error", "El legajo ingresado ya existe").showAndWait();
                    return;
                }
            } else {
                Student newStudent = studentService.updateSudent(student);
                if(newStudent == null) {
                    getErrorDialog("Error", "El legajo ingresado ya existe").showAndWait();
                    return;
                }
            }
            stage.close();
        });

        newParentButton.setOnAction(event -> {
            cuilModal[0] = getNewCuilModal(stage, student.getParentsCuil());
            cuilModal[0].setOnHiding(newEvent -> {
                data.removeAll(data);
                data.addAll(FXCollections.observableArrayList(student.getParentsCuil()));
            });
            cuilModal[0].show();
        });

        deleteParentButton.setOnAction(event -> {
            if(table.getSelectionModel().getFocusedIndex() > -1) {
                student.getParentsCuil().remove(table.getSelectionModel().getSelectedItem());
                data.removeAll(data);
                data.addAll(FXCollections.observableArrayList(student.getParentsCuil()));
            }
        });

        return stage;
    }

    public Stage getNewCuilModal(Stage parentStage, List<String> cuils) {
        StackPane stackPane = new StackPane();

        Label cuilLabel = new Label("Inserte número de CUIL");

        TextField cuilInput = new TextField ();
        cuilInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                cuilInput.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (cuilInput.getText().length() > 11) {
                String s = cuilInput.getText().substring(0, 11);
                cuilInput.setText(s);
            }
        });

        Button cacelCuilButton = new Button();
        cacelCuilButton.setText("Cancelar");
        AnchorPane.setLeftAnchor(cacelCuilButton, 0d);
        AnchorPane.setRightAnchor(cacelCuilButton, 0d);

        Button acceptCuilButton = new Button();
        acceptCuilButton.setText("Aceptar");
        acceptCuilButton.setDisable(true);
        AnchorPane.setLeftAnchor(acceptCuilButton, 0d);
        AnchorPane.setRightAnchor(acceptCuilButton, 0d);

        AnchorPane anchorPaneCancel = new AnchorPane();
        anchorPaneCancel.getChildren().add(cacelCuilButton);

        AnchorPane anchorPaneAccept = new AnchorPane();
        anchorPaneAccept.getChildren().add(acceptCuilButton);

        cuilInput.textProperty().addListener((observable, oldValue, newValue) -> {
            acceptCuilButton.setDisable(newValue.isEmpty());
        });

        VBox vBox = new VBox();
        vBox.getChildren().addAll(cuilLabel, cuilInput, anchorPaneAccept, anchorPaneCancel);
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10));

        Stage stage = new Stage();
        stage.setTitle("CUIL de responsable");
        stackPane.getChildren().add(vBox);
        stage.setScene(new Scene(stackPane, 280,142));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(parentStage);
        stage.setResizable(false);

        cacelCuilButton.setOnAction(event -> {
            stage.close();
        });

        acceptCuilButton.setOnAction(event -> {
            cuils.add(cuilInput.getText());
            stage.close();
        });

        return stage;
    }

    public Stage getIdCompleteModal(Stage parentStage) {

        StackPane stackPane = new StackPane();
        Stage stage = new Stage();
        VBox vBox = new VBox();

        Label pathLabel = new Label("Ruta de archivo");
        Label keyWordLabel = new Label("Nombre de columna a analizar");

        TextField pathInput = new TextField ();
        TextField keyWordInput = new TextField ();

        Button fileSelectorButton = new Button();
        Button completeButton = new Button();
        Button backButton = new Button();

        AnchorPane anchorPaneFileSelector = new AnchorPane();
        AnchorPane anchorPaneComplete = new AnchorPane();
        AnchorPane anchorPaneProgressBar = new AnchorPane();
        AnchorPane anchorPaneBack = new AnchorPane();

        ProgressBar progressBar = new ProgressBar(0);

        FileChooser fileChooser = new FileChooser();

        pathInput.textProperty().addListener((observable, oldValue, newValue) -> {
            completeButton.setDisable(newValue.isEmpty());
        });

        fileSelectorButton.setText("Examinar");
        keyWordInput.setText("Concepto");
        backButton.setText("Volver");
        completeButton.setText("Cargar números de legajo");
        completeButton.setDisable(true);

        AnchorPane.setLeftAnchor(fileSelectorButton, 0d);
        AnchorPane.setRightAnchor(fileSelectorButton, 0d);
        AnchorPane.setLeftAnchor(completeButton, 0d);
        AnchorPane.setRightAnchor(completeButton, 0d);
        AnchorPane.setLeftAnchor(progressBar, 0d);
        AnchorPane.setRightAnchor(progressBar, 0d);
        AnchorPane.setLeftAnchor(backButton, 0d);
        AnchorPane.setRightAnchor(backButton, 0d);

        anchorPaneFileSelector.getChildren().add(fileSelectorButton);
        anchorPaneComplete.getChildren().add(completeButton);
        anchorPaneProgressBar.getChildren().add(progressBar);
        anchorPaneBack.getChildren().add(backButton);

        vBox.getChildren().addAll(pathLabel, pathInput, anchorPaneFileSelector, keyWordLabel, keyWordInput, anchorPaneProgressBar, anchorPaneComplete, anchorPaneBack);
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10));
        stackPane.getChildren().add(vBox);

        stage.setTitle("Completar planilla de Excel");
        stage.setScene(new Scene(stackPane, 500, 300));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(parentStage);
        stage.setResizable(false);

        fileChooser.setTitle("Seleccionar archivo");

        fileSelectorButton.setOnAction(event -> {
            File file = fileChooser.showOpenDialog(stage);
            if(file != null) {
                pathInput.setText(file.getPath());
            }
        });

        completeButton.setOnAction(event -> {
            fileSelectorButton.setDisable(true);
            completeButton.setDisable(true);
            pathInput.setDisable(true);
            keyWordInput.setDisable(true);
            backButton.setDisable(true);
            Boolean status = excelService.runCompleteExcel(pathInput.getText(), 0, keyWordInput.getText());
            if(Boolean.TRUE.equals(status)) {
                progressBar.setProgress(100);
                backButton.setDisable(false);
                getInformationDialog("Legajos completados", "¡Los números de legajo se cargaron correctamente!").showAndWait();
            } else {
                fileSelectorButton.setDisable(false);
                completeButton.setDisable(false);
                pathInput.setDisable(false);
                keyWordInput.setDisable(false);
                backButton.setDisable(false);
            }
        });

        backButton.setOnAction(event -> {
            stage.close();
        });

        return stage;
    }

    public static Alert getErrorDialog(String title, String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text);
        return alert;
    }

    public static Alert getInformationDialog(String title, String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text);
        return alert;
    }

    public static Alert getConfirmationDialog(String title, String text) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text);
        return alert;
    }

}
