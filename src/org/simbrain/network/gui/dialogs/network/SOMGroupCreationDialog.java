/*
 * Part of Simbrain--a java-based neural network kit
 * Copyright (C) 2005,2007 The Authors.  See http://www.simbrain.net/credits
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.simbrain.network.gui.dialogs.network;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.simbrain.network.gui.NetworkPanel;
import org.simbrain.network.gui.dialogs.layout.MainLayoutPanel;
import org.simbrain.network.gui.dialogs.network.SOMPropertiesPanel.SOMPropsPanelType;
import org.simbrain.network.subnetworks.SOMGroup;
import org.simbrain.util.StandardDialog;
import org.simbrain.util.widgets.ShowHelpAction;

/**
 * <b>SOMDialog</b> is used as an assistant to create SOM networks.
 */
public class SOMGroupCreationDialog extends StandardDialog {

    /** Tabbed pane. */
    private JTabbedPane tabbedPane = new JTabbedPane();

    /** Logic tab panel. */
    private JPanel tabLogic = new JPanel();

    /** Layout tab panel. */
    private JPanel tabLayout = new JPanel();

    /** SOM properties panel. */
    private SOMPropertiesPanel somPanel;

    /** Layout panel. */
    private MainLayoutPanel layoutPanel;

    /** Network Panel. */
    private NetworkPanel networkPanel;

    /**
     * This method is the default constructor.
     *
     * @param networkPanel Network panel
     */
    public SOMGroupCreationDialog(final NetworkPanel networkPanel) {
        this.networkPanel = networkPanel;
        layoutPanel = new MainLayoutPanel(false, this);
        init();
    }

    /**
     * Initializes all components used in dialog.
     */
    private void init() {

        setTitle("New SOM Group");
        somPanel = new SOMPropertiesPanel(networkPanel,
                SOMPropsPanelType.CREATE_GROUP);

        // Set up tab panels
        tabLogic.add(somPanel);
        layoutPanel = new MainLayoutPanel(false, this);
        layoutPanel.setCurrentLayout(SOMGroup.DEFAULT_LAYOUT);
        tabLayout.add(layoutPanel);
        tabbedPane.addTab("Logic", tabLogic);
        tabbedPane.addTab("Layout", layoutPanel);
        setContentPane(tabbedPane);

        // Help action
        Action helpAction = new ShowHelpAction(somPanel.getHelpPath());
        addButton(new JButton(helpAction));

    }

    /**
     * Called when dialog closes.
     */
    @Override
    protected void closeDialogOk() {
        somPanel.commitChanges();
        SOMGroup som = (SOMGroup) somPanel.getGroup();
        layoutPanel.commitChanges();
        som.setLayout(layoutPanel.getCurrentLayout());
        som.applyLayout();
        som.offset(networkPanel.getWhereToAdd().getX(), networkPanel
                .getWhereToAdd().getY());
        networkPanel.getNetwork().addGroup(som);
        networkPanel.getNetwork().fireNeuronsUpdated(); //todo: why won't fireGroupUpdated(som) work (label not created).
        super.closeDialogOk();

    }

}
