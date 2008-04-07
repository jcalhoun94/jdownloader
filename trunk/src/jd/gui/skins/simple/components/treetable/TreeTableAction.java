//    jDownloader - Downloadmanager
//    Copyright (C) 2008  JD-Team jdownloader@freenet.de
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program  is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSSee the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://wnu.org/licenses/>.


package jd.gui.skins.simple.components.treetable;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;

import jd.config.Property;
import jd.utils.JDLocale;


public class TreeTableAction extends AbstractAction{


    public static final int DOWNLOAD_INFO = 0;
    public static final int DOWNLOAD_DOWNLOAD_DIR = 1;
    public static final int DOWNLOAD_DELETE = 2;
    public static final int DOWNLOAD_FORCE = 3;
    public static final int DOWNLOAD_RESET = 4;
    public static final int DOWNLOAD_ENABLE = 5;
    public static final int DOWNLOAD_DISABLE = 6;
    public static final int DOWNLOAD_BROWSE_LINK = 7;
    public static final int DOWNLOAD_NEW_PACKAGE = 8;
    public static final int PACKAGE_INFO = 9;
    public static final int PACKAGE_EDIT_DIR = 10;
    public static final int PACKAGE_EDIT_NAME = 11;
    public static final int PACKAGE_DOWNLOAD_DIR = 12;
    public static final int PACKAGE_DELETE = 13;
    public static final int PACKAGE_ENABLE = 14;
    public static final int PACKAGE_DISABLE = 15;
    public static final int PACKAGE_RESET = 16;
    public static final int PACKAGE_SORT = 17;
    private ActionListener actionListener;
    private int actionID;

    private String ressourceName;
    private Property property;

    public TreeTableAction(ActionListener actionListener, String ressourceName, int actionID){      
        this(actionListener, ressourceName, actionID, null);
      

    }
    public TreeTableAction(ActionListener actionListener, String ressourceName, int actionID, Property obj) {
        super();
        this.ressourceName = ressourceName;
        this.actionID = actionID;
        this.actionListener = actionListener; 
        this.property=obj;

    
        putValue(Action.NAME,              JDLocale.L("gui.table.contextmenu."+ressourceName));
    
    }
    public void actionPerformed(ActionEvent e) {
        actionListener.actionPerformed(new ActionEvent(e.getSource(),actionID,ressourceName));
    }
    public int getActionID(){
        return actionID;
    }
    public Property getProperty() {
        return property;
    }
    public void setProperty(Property property) {
        this.property = property;
    }

}
