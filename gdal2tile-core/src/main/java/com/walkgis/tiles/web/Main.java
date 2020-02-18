package com.walkgis.tiles.web;

import java.util.Observable;
import java.util.Observer;

public class Main {
    public static class RealSubject extends Observable {

        public void makeChanged() {
            setChanged();
            notifyObservers();
        }
    }

    public static class RealObserver implements Observer {

        @Override
        public void update(Observable o, Object arg) {
            System.out.println("调用了-->");
        }
    }

    public static void main(String[] args) {
        RealSubject subject = new RealSubject();
        subject.addObserver(new RealObserver());
        subject.makeChanged();
    }
}
