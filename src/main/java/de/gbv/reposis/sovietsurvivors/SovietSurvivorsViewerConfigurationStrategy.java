package de.gbv.reposis.sovietsurvivors;

import org.mycore.frontend.MCRFrontendUtil;
import org.mycore.mir.viewer.MIRViewerConfigurationStrategy;
import org.mycore.viewer.configuration.MCRViewerConfiguration;

import jakarta.servlet.http.HttpServletRequest;

public class SovietSurvivorsViewerConfigurationStrategy extends MIRViewerConfigurationStrategy {

    @Override
    public MCRViewerConfiguration get(HttpServletRequest request) {
        MCRViewerConfiguration defaultConfig = super.get(request);

        defaultConfig.setProperty("text.showOnStart", "transcription");
        defaultConfig.setProperty("chapter.showOnStart", false);
        defaultConfig.setProperty("actionGroupEnabled", true);

        if(request.getParameter("frame") != null && request.getParameter("frame").equals("true")) {
            defaultConfig.addScript(MCRFrontendUtil.getBaseURL() + "assets/bootstrap/js/bootstrap.min.js");
        }

        return defaultConfig;
    }
}
