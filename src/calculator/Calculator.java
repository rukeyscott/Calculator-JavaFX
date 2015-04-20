/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculator;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
 
import java.util.HashMap;
import java.util.Map;
 

public class Calculator extends Application {
    //create all of the items shown in the calculator
  private static final String[][] template = {
      {"CE","+/-","%", "/"},
      { "7", "8", "9", "*" },
      { "4", "5", "6", "-" },
      { "1", "2", "3", "+" },
      { "0", " ", ".", "=" }
  };
 
  private final Map<String, Button> accelerators = new HashMap<>();
  double val=0;
 
  private DoubleProperty stackValue = new SimpleDoubleProperty();
  private DoubleProperty value = new SimpleDoubleProperty();
 
  private enum Op { NOOP, ADD, SUBTRACT, MULTIPLY, DIVIDE,PERCENT,DECIMAL,REVERSE }
 
  private Op curOp   = Op.NOOP;
  private Op stackOp = Op.NOOP;
 
  public static void main(String[] args) { launch(args); }
 
  @Override public void start(Stage stage) {
    final TextField screen  = createScreen();
    final TilePane  buttons = createButtons();
 
    stage.setTitle("Java Calculator");
    stage.initStyle(StageStyle.UTILITY);
    stage.setResizable(false);
    stage.setScene(new Scene(createLayout(screen, buttons)));
    stage.show();
  }
 
  
  //create a vbox layout
  private VBox createLayout(TextField screen, TilePane buttons) {
    final VBox layout = new VBox(40);
    layout.setAlignment(Pos.CENTER);
    layout.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-font-size: 40;");
    layout.getChildren().setAll(screen, buttons);
    handleAccelerators(layout);
    screen.prefWidthProperty().bind(buttons.widthProperty());
    return layout;
  }//end vbox
 
  private void handleAccelerators(VBox layout) {
    layout.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent keyEvent) {
        Button activated = accelerators.get(keyEvent.getText());
        if (activated != null) {
          activated.fire();
        }
      }
    });
  }//end accelerators
  
  
 //creates the text feild
  private TextField createScreen() {
    final TextField screen = new TextField();
    screen.setStyle("-fx-background-color: grey;-fx-background-radius: 8.0;-fx-border-width: 2px;");
    screen.setAlignment(Pos.CENTER_RIGHT);
    screen.setEditable(false);
    screen.textProperty().bind(Bindings.format("%.0f", value));
    return screen;
  }//end craetescreen
  
  
 // creates the tilepane
  private TilePane createButtons() {
    TilePane buttons = new TilePane();
    buttons.setVgap(8);
    buttons.setHgap(8);
    buttons.setPrefColumns(template[0].length);
    for (String[] r: template) {
      for (String s: r) {
        buttons.getChildren().add(createButton(s));
      }
    }
    return buttons;
  }//end create tilepane
  
  
 //creates the buttons
  private Button createButton(final String s) {
    Button button = makeStandardButton(s);
 
    if (s.matches("[0-9]" )) {
      makeNumericButton(s, button);
    }
    
    else {
      final ObjectProperty<Op> triggerOp = determineOperand(s);
      if (triggerOp.get() != Op.NOOP) {
        makeOperandButton(button, triggerOp);
      } else if ("C".equals(s)) {
        makeClearButton(button);
      } else if ("CE".equals(s)) {
        makeClearAButton(button);
      }else if ("=".equals(s)) {
        makeEqualsButton(button);
      }if(".".equals(s))
        makeDecimalButton(s,button);
      {
        if("+/-".equals(s))
        makeReverseButton(s,button);
        
      }
    }
    
 
    return button;
  }//end create button
 
  private ObjectProperty<Op> determineOperand(String s) {
    final ObjectProperty<Op> triggerOp = new SimpleObjectProperty<>(Op.NOOP);
    switch (s) {
      case "%": triggerOp.set(Op.PERCENT);      break;
      case ".": triggerOp.set(Op.DECIMAL);      break;
      case "+/-": triggerOp.set(Op.REVERSE);      break;
      case "+": triggerOp.set(Op.ADD);      break;
      case "-": triggerOp.set(Op.SUBTRACT); break;
      case "*": triggerOp.set(Op.MULTIPLY); break;
      case "/": triggerOp.set(Op.DIVIDE);   break;
     
    }
    return triggerOp;
  }//end determine
 
  private void makeOperandButton(Button button, final ObjectProperty<Op> triggerOp) {
    button.setStyle("-fx-base: chocolate;");
    button.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        curOp = triggerOp.get();
      }
    });
  }//end operand
 
  private Button makeStandardButton(String s) {
    Button button = new Button(s);
    button.setStyle("-fx-base: white;");
    accelerators.put(s, button);
    button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    return button;
  }// end standard
 
  private void makeNumericButton(final String s, Button button) {
    button.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        if (curOp == Op.NOOP) {
          value.set(value.get() * 10 + Double.parseDouble(s));
        } else {
          stackValue.set(value.get());
          value.set(Double.parseDouble(s));
          stackOp = curOp;
          curOp = Op.NOOP;
        }
      }
    });
  }//end numeric
 
  private void makeClearButton(Button button) {
    button.setStyle("-fx-base: white;");
    button.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        value.set(0);
      }
    });
  }//end clear
  
  
   private void makeClearAButton(Button button) {
    button.setStyle("-fx-base: white;");
    button.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        value.set(0);
      }
    });
  }//end clear
   
  private void makeReverseButton(final String s,Button button) {
      
      button.setOnAction(new EventHandler<ActionEvent>() {
       @Override
      public void handle(ActionEvent actionEvent) {
        value.set(0);
      }
    });
  }
    
 

  private void makeDecimalButton(final String s,Button button) {
     button.setOnAction(new EventHandler<ActionEvent>() {
       @Override
      public void handle(ActionEvent actionEvent) {
        value.set(0);
      }
    });
  }//end decimal
 
  private void makeEqualsButton(Button button) {
    button.setStyle("-fx-base: chocolate;");
    button.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        switch (stackOp) {
          case PERCENT:      value.set(100 / stackValue.get()); break;
          case REVERSE:      value.set(100 / stackValue.get()); break;
          case DECIMAL:      value.set(100 / stackValue.get()); break;
          case ADD:      value.set(stackValue.get() + value.get()); break;
          case SUBTRACT: value.set(stackValue.get() - value.get()); break;
          case MULTIPLY: value.set(stackValue.get() * value.get()); break;
          case DIVIDE:   value.set(stackValue.get() / value.get()); break;
          
        }
      }
    });
  }//end equals
  
  
}