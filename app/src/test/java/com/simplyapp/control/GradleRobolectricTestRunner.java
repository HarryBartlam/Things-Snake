package com.simplyapp.control;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.manifest.AndroidManifest;
import org.robolectric.res.FileFsFile;
import org.robolectric.util.Logger;
import org.robolectric.util.ReflectionHelpers;

public class GradleRobolectricTestRunner extends RobolectricTestRunner {
    private static final String BUILD_OUTPUT = "build/intermediates";

    public GradleRobolectricTestRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected AndroidManifest getAppManifest(Config config) {
        if (config.constants() == Void.class) {
            Logger.error("Field 'constants' not specified in @Config annotation");
            Logger.error("This is required when using RobolectricGradleTestRunner!");
            throw new RuntimeException("No 'constants' field in @Config annotation!");
        }

        final String type = getType(config);
        final String flavor = getFlavor(config);
        final String packageName = getPackageName(config);

        final FileFsFile res;
        final FileFsFile assets;
        final FileFsFile manifest;

        String directory = BUILD_OUTPUT;

        if (!FileFsFile.from(BUILD_OUTPUT, "manifests").exists()) {
            directory = "app/" + BUILD_OUTPUT;
        }

        // res/merged added in Android Gradle plugin 1.3-beta1
        if (FileFsFile.from(directory, "res", "merged").exists()) {
            res = FileFsFile.from(directory, "res", "merged", flavor, type);
        } else if (FileFsFile.from(directory, "res").exists()) {
            res = FileFsFile.from(directory, "res", flavor, type);
        } else {
            res = FileFsFile.from(directory, "bundles", flavor, type, "res");
        }

        if (FileFsFile.from(directory, "assets").exists()) {
            assets = FileFsFile.from(directory, "assets", flavor, type);
        } else {
            assets = FileFsFile.from(directory, "bundles", flavor, type, "assets");
        }

        if (FileFsFile.from(directory, "manifests").exists()) {
            manifest = FileFsFile.from(directory, "manifests", "full", flavor, type, "AndroidManifest.xml");
        } else {
            manifest = FileFsFile.from(directory, "bundles", flavor, type, "AndroidManifest.xml");
        }

        Logger.debug("Robolectric assets directory: " + assets.getPath());
        Logger.debug("   Robolectric res directory: " + res.getPath());
        Logger.debug("   Robolectric manifest path: " + manifest.getPath());
        Logger.debug("    Robolectric package name: " + packageName);
        return new AndroidManifest(manifest, res, assets, packageName);
    }

    private static String getType(Config config) {
        try {
            return ReflectionHelpers.getStaticField(config.constants(), "BUILD_TYPE");
        } catch (Throwable e) {
            return null;
        }
    }

    private static String getFlavor(Config config) {
        try {
            return ReflectionHelpers.getStaticField(config.constants(), "FLAVOR");
        } catch (Throwable e) {
            return null;
        }
    }

    private static String getPackageName(Config config) {
        try {
            final String packageName = config.packageName();
            if (packageName != null && !packageName.isEmpty()) {
                return packageName;
            } else {
                return ReflectionHelpers.getStaticField(config.constants(), "APPLICATION_ID");
            }
        } catch (Throwable e) {
            return null;
        }
    }
}

