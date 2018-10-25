package org.arabellan.tdas;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Main {

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new GuiceModule());
        injector.getInstance(Application.class).run();
    }

}
