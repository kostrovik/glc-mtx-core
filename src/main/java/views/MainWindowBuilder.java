package views;

import com.sun.javafx.scene.control.skin.ScrollBarSkin;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.EventObject;

/**
 * project: glc-mtx-core
 * author:  kostrovik
 * date:    04/07/2018
 * github:  https://github.com/kostrovik/glc-mtx-core
 */
public class MainWindowBuilder implements ContentBuilderInterface, ContentViewInterface {
    private Pane parent;

    public MainWindowBuilder(EventObject event, Pane parent) {
        this.parent = parent;
    }

    @Override
    public ContentViewInterface build(EventObject event, Pane content) {
        return this;
    }

    @Override
    public void initView(EventObject event) {

    }

    @Override
    public Region getView() {
        ScrollPane view = new ScrollPane();
        view.setBackground(Background.EMPTY);

        view.prefWidthProperty().bind(parent.widthProperty());
        view.prefHeightProperty().bind(parent.heightProperty());

        Text value = new Text();
        value.setId("page-text");
        value.setText("Для начала работы обратитесь к основному меню.");
        value.setFont(Font.font(18));

        StackPane textHolder = new StackPane(value);

        textHolder.prefWidthProperty().bind(parent.widthProperty().subtract(ScrollBarSkin.DEFAULT_WIDTH));
        textHolder.prefHeightProperty().bind(parent.heightProperty().subtract(ScrollBarSkin.DEFAULT_WIDTH));

        view.setContent(textHolder);

        view.viewportBoundsProperty().addListener((arg0, arg1, arg2) -> {
            Node content = view.getContent();
            view.setFitToWidth(content.prefWidth(-1) < arg2.getWidth());
            view.setFitToHeight(content.prefHeight(-1) < arg2.getHeight());
        });

        return view;
    }
}
