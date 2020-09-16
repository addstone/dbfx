package cn.navigational.dbfx;

import cn.navigational.dbfx.kit.utils.VertxUtils;
import cn.navigational.dbfx.model.UiPreferences;
import cn.navigational.dbfx.view.SplashView;
import io.vertx.core.VertxOptions;
import io.vertx.core.file.FileSystemOptions;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Javafx start class
 *
 * @author yangkui
 * @since 1.0
 */
public class Launcher extends Application {

    private final static Logger LOG = LoggerFactory.getLogger(Launcher.class);

    /**
     * Global UI preference
     */
    public static UiPreferences uiPreference;

    @Override
    public void init() {
        LOG.debug("Start init dbfx options.");
        var vertxOptions = new VertxOptions();
        var fsOptions = new FileSystemOptions();
        //Disable file cached
        fsOptions.setFileCachingEnabled(false);
        //Worker pool size
        vertxOptions.setWorkerPoolSize(10);
        vertxOptions.setFileSystemOptions(fsOptions);
        VertxUtils.initVertx(vertxOptions);
    }

    @Override
    public void start(Stage primaryStage) {
        new SplashView();
    }

    @Override
    public void stop() throws Exception {
        LOG.debug("Stop current application.");
        super.stop();
        VertxUtils.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
