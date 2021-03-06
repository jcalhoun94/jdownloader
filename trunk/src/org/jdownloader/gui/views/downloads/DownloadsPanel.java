package org.jdownloader.gui.views.downloads;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.JScrollPane;

import jd.Main;
import jd.controlling.downloadcontroller.DownloadController;
import jd.controlling.downloadcontroller.DownloadControllerEvent;
import jd.controlling.downloadcontroller.DownloadControllerListener;
import jd.gui.swing.jdgui.interfaces.SwitchPanel;
import net.miginfocom.swing.MigLayout;

import org.appwork.utils.swing.EDTRunner;
import org.jdownloader.gui.views.downloads.table.DownloadsTable;
import org.jdownloader.gui.views.downloads.table.DownloadsTableModel;

public class DownloadsPanel extends SwitchPanel implements DownloadControllerListener {

    /**
     * 
     */
    private static final long   serialVersionUID = -2610465878903778445L;
    private DownloadsTable      table;
    private JScrollPane         tableScrollPane;
    private DownloadsTableModel tableModel;
    private ScheduledFuture<?>  timer            = null;
    private BottomBar           bottomBar;

    public DownloadsPanel() {
        super(new MigLayout("ins 0, wrap 1", "[grow, fill]", "[grow, fill]"));
        tableModel = new DownloadsTableModel();
        table = new DownloadsTable(tableModel);
        tableScrollPane = new JScrollPane(table);
        tableScrollPane.setBorder(null);
        this.add(tableScrollPane, "cell 0 0");
        Main.GUI_COMPLETE.executeWhenReached(new Runnable() {

            public void run() {
                new EDTRunner() {

                    @Override
                    protected void runInEDT() {
                        bottomBar = new BottomBar(table);
                        add(bottomBar, "dock south,hidemode 3");
                    }
                };
            }

        });

    }

    @Override
    protected void onShow() {
        tableModel.recreateModel(false);
        synchronized (this) {
            if (timer != null) timer.cancel(false);
            timer = tableModel.getThreadPool().scheduleWithFixedDelay(new Runnable() {

                public void run() {
                    tableModel.refreshModel();
                }

            }, 250, 1000, TimeUnit.MILLISECONDS);
        }
        DownloadController.getInstance().addListener(this);
        table.requestFocusInWindow();
    }

    @Override
    protected void onHide() {
        synchronized (this) {
            if (timer != null) {
                timer.cancel(false);
                timer = null;
            }
        }
        DownloadController.getInstance().removeListener(this);
    }

    public void onDownloadControllerEvent(DownloadControllerEvent event) {
        switch (event.getType()) {
        case REFRESH_STRUCTURE:
        case REMOVE_CONTENT:
            tableModel.recreateModel();
            break;
        case REFRESH_DATA:
            tableModel.refreshModel();
            break;
        }
    }

}
