package com.example.framework;

/**
 * Created by taylorkern1 on 17/10/2016.
 */

public abstract class FrameworkCopyright {

        private String CopyrightText;
        protected abstract String getAppCopyright();

        protected FrameworkCopyright() {
            CopyrightText = "This application is based on the Simple Android Application Framework. (c) University of East Anglia 2016.";
        }

        public final String getCopyright() {
            return CopyrightText + "\n\n" + getAppCopyright();
        }

}
