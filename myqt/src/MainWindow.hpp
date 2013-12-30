#ifndef _MAINWINDOW_HPP
#define _MAINWINDOW_HPP

#include "MainWindow.ui.h"

class MainWindow : public QDialog {
    Q_OBJECT
public:
    MainWindow();
    virtual ~MainWindow();
private:
    Ui::MainWindow widget;
};

#endif
