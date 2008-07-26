package jd.plugins.host;

import java.io.File;
import java.net.URL;
import java.util.regex.Pattern;
import jd.parser.Regex;
import jd.plugins.DownloadLink;
import jd.plugins.HTTP;
import jd.plugins.HTTPConnection;
import jd.plugins.PluginForHost;
import jd.plugins.PluginStep;
import jd.plugins.RequestInfo;
import jd.plugins.download.RAFDownload;
import jd.utils.JDUtilities;

public class SharedZillacom extends PluginForHost {
    private static final String CODER = "JD-Team";

    private static final String HOST = "sharedzilla.com";

    private static final String PLUGIN_NAME = HOST;

    private static final String PLUGIN_VERSION = "1.0.0.0";

    private static final String PLUGIN_ID = PLUGIN_NAME + "-" + PLUGIN_VERSION;

    static private final Pattern PAT_SUPPORTED = Pattern.compile("http://[\\w\\.]*?sharedzilla\\.com/(en|ru)/get\\?id=\\d+", Pattern.CASE_INSENSITIVE);
    private RequestInfo requestInfo;
    private String passCode = "";

    public SharedZillacom() {
        super();
        steps.add(new PluginStep(PluginStep.STEP_COMPLETE, null));
    }

    @Override
    public boolean doBotCheck(File file) {
        return false;
    }

    @Override
    public String getCoder() {
        return CODER;
    }

    @Override
    public String getPluginName() {
        return PLUGIN_NAME;
    }

    @Override
    public String getHost() {
        return HOST;
    }

    @Override
    public String getVersion() {
        return PLUGIN_VERSION;
    }

    @Override
    public String getPluginID() {
        return PLUGIN_ID;
    }

    @Override
    public Pattern getSupportedLinks() {
        return PAT_SUPPORTED;
    }

    @Override
    public void reset() {
    }

    @Override
    public int getMaxSimultanDownloadNum() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean getFileInformation(DownloadLink downloadLink) {
        try {
            requestInfo = HTTP.getRequest(new URL(downloadLink.getDownloadURL()));
            if (!requestInfo.containsHTML("Upload not found")) {
                String filename = new Regex(requestInfo.getHtmlCode(), Pattern.compile("nowrap title=\"(.*?)\">", Pattern.CASE_INSENSITIVE)).getFirstMatch();
                String filesize = new Regex(requestInfo.getHtmlCode(), Pattern.compile("<span title=\"(.*?) Bytes\">", Pattern.CASE_INSENSITIVE)).getFirstMatch();
                if (filesize != null) {
                    downloadLink.setDownloadMax(new Integer(filesize));
                }
                downloadLink.setName(filename);
                return true;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        downloadLink.setAvailable(false);
        return false;
    }

    public PluginStep doStep(PluginStep step, DownloadLink downloadLink) {
        if (step == null) return null;
        try {
            /* Nochmals das File überprüfen */
            if (!getFileInformation(downloadLink)) {
                downloadLink.setStatus(DownloadLink.STATUS_ERROR_FILE_NOT_FOUND);
                step.setStatus(PluginStep.STATUS_ERROR);
                return step;
            }
            /* ID holen */
            String id = new Regex(downloadLink.getDownloadURL(), Pattern.compile("get\\?id=(\\d+)", Pattern.CASE_INSENSITIVE)).getFirstMatch();
            /* Password checken */
            if (requestInfo.containsHTML("Password protected")) {
                if (downloadLink.getStringProperty("pass", null) == null) {
                    if ((passCode = JDUtilities.getGUI().showUserInputDialog("Code?")) == null) passCode = "";
                }else{
                    /*gespeicherten PassCode holen*/
                    passCode=downloadLink.getStringProperty("pass", null);
                }
            }
            /* Free Download starten */
            requestInfo = HTTP.postRequestWithoutHtmlCode(new URL("http://sharedzilla.com/en/downloaddo"), requestInfo.getCookie(), downloadLink.getDownloadURL(), "id=" + id + "&upload_password=" + JDUtilities.urlEncode(passCode), false);
            if (requestInfo.getLocation() == null) {
                requestInfo = HTTP.postRequest(new URL("http://sharedzilla.com/en/downloaddo"), requestInfo.getCookie(), downloadLink.getDownloadURL(), null, "id=" + id + "&upload_password=" + JDUtilities.urlEncode(passCode), false);
                if (requestInfo.containsHTML("<p>Password is wrong!</p>")) {
                    /*PassCode war falsch, also Löschen*/
                    downloadLink.setProperty("pass", null);
                }
                step.setStatus(PluginStep.STATUS_ERROR);
                downloadLink.setStatus(DownloadLink.STATUS_ERROR_CAPTCHA_WRONG);
                return step;
            }
            /*PassCode war richtig, also Speichern*/
            downloadLink.setProperty("pass", passCode);
            requestInfo = HTTP.getRequestWithoutHtmlCode(new URL(requestInfo.getLocation()), requestInfo.getCookie(), downloadLink.getDownloadURL(), false);
            /* Datei herunterladen */
            HTTPConnection urlConnection = requestInfo.getConnection();
            String filename = getFileNameFormHeader(urlConnection);
            if (urlConnection.getContentLength() == 0) {
                downloadLink.setStatus(DownloadLink.STATUS_ERROR_TEMPORARILY_UNAVAILABLE);
                step.setStatus(PluginStep.STATUS_ERROR);
                return step;
            }
            downloadLink.setDownloadMax(urlConnection.getContentLength());
            downloadLink.setName(filename);
            long length = downloadLink.getDownloadMax();
            dl = new RAFDownload(this, downloadLink, urlConnection);
            dl.setFilesize(length);
            dl.setChunkNum(1);/*
                               * bei dem speed lohnen mehrere chunks nicht, da
                               * es nicht schneller wird
                               */
            dl.setResume(true);
            if (!dl.startDownload() && step.getStatus() != PluginStep.STATUS_ERROR && step.getStatus() != PluginStep.STATUS_TODO) {
                downloadLink.setStatus(DownloadLink.STATUS_ERROR_TEMPORARILY_UNAVAILABLE);
                step.setStatus(PluginStep.STATUS_ERROR);
                return step;
            }
            return step;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        step.setStatus(PluginStep.STATUS_ERROR);
        downloadLink.setStatus(DownloadLink.STATUS_ERROR_UNKNOWN);
        return step;
    }

    @Override
    public void resetPluginGlobals() {
        // TODO Auto-generated method stub
    }

    @Override
    public String getAGBLink() {
        return "http://sharedzilla.com/en/terms";
    }

}
