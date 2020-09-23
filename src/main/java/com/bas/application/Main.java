package com.bas.application;

import com.bas.component.Components;
import javafx.stage.Stage;

public class Main extends javafx.application.Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage principalStage) throws Exception{
        Components components = new Components();
        components.initPrincipal(principalStage);
    }

}
