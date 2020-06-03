import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.text.Text;
import javafx.scene.control.TextField;

public class Controller {

    @FXML private Text value;
    @FXML private TextField to;
    @FXML private TextField from;
    @FXML private TextField weight;
    @FXML private CheckBox checkBox;
    private String space = "";
    private VGraph vGraph = VGraph.getInstance();

    public void next(ActionEvent actionEvent) {
        if (from.getText().equals("") || to.getText().equals("") || weight.getText().equals("")) return;
        vGraph.addEdge(from.getText(),to.getText(),weight.getText()+space);
        clear();
        vGraph.update();
        value.setText("");
        space+=" ";
    }

    public void back(ActionEvent actionEvent) {
        vGraph.back();
        vGraph.update();
        value.setText("");
    }

    public void solve(ActionEvent actionEvent) {
        value.setText(vGraph.solve());
    }

    public void clear(ActionEvent actionEvent) {
        clear();
        vGraph.clear();
        vGraph.update();
        value.setText("");
    }

    private void clear(){
        to.clear(); from.clear(); weight.clear();
    }

    public void check(ActionEvent actionEvent) {
        if (checkBox.isSelected()) {
            vGraph.auto(true);
        } else {
            vGraph.auto(false);
        }
    }
}
