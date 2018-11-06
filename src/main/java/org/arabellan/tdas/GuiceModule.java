package org.arabellan.tdas;

import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.arabellan.tdas.model.Configuration;
import org.arabellan.tdas.utils.FileIO;

import java.io.IOException;

class GuiceModule extends AbstractModule {

    protected void configure() {
        bind(EntityService.class).asEagerSingleton();
        bind(LevelService.class).asEagerSingleton();
    }

    @Provides
    @Singleton
    Gson provideGson() {
        return new Gson();
    }

    @Provides
    @Singleton
    Configuration provideConfiguration(FileIO fileIO, Gson gson) throws IOException {
        String json = fileIO.loadFileAsString("/config.json");
        return gson.fromJson(json, Configuration.class);
    }

}
