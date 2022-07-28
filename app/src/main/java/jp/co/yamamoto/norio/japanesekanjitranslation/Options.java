package jp.co.yamamoto.norio.japanesekanjitranslation;

import android.app.Application;

public class Options extends Application {
    public boolean isPaste() {
        return paste;
    }

    public void setPaste(boolean paste) {
        this.paste = paste;
    }

    public boolean isCopy() {
        return copy;
    }

    public void setCopy(boolean copy) {
        this.copy = copy;
    }

    public boolean isUseYahooApi() {
        return useYahooApi;
    }

    public void setUseYahooApi(boolean useYahooApi) {
        this.useYahooApi = useYahooApi;
    }

    public boolean isUseRoman() {
        return useRoman;
    }

    public void setUseRoman(boolean useRoman) {
        this.useRoman = useRoman;
    }

    public boolean isRemoveSpace() {
        return removeSpace;
    }

    public void setRemoveSpace(boolean removeSpace) {
        this.removeSpace = removeSpace;
    }

    private boolean paste = false;
    private boolean copy = false;
    private boolean useYahooApi = false;
    private boolean useRoman = false;
    private boolean removeSpace = false;
}
