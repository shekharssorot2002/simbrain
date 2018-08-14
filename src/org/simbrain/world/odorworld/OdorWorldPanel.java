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
package org.simbrain.world.odorworld;

import org.piccolo2d.PCanvas;
import org.piccolo2d.PNode;
import org.piccolo2d.event.PInputEventListener;
import org.piccolo2d.event.PMouseWheelZoomEventHandler;
import org.simbrain.network.gui.nodes.SelectionHandle;
import org.simbrain.util.SceneGraphBrowser;
import org.simbrain.util.StandardDialog;
import org.simbrain.util.piccolo.Sprite;
import org.simbrain.workspace.gui.CouplingMenu;
import org.simbrain.world.odorworld.actions.*;
import org.simbrain.world.odorworld.entities.OdorWorldEntity;
import org.simbrain.world.odorworld.gui.DragEventHandler2;
import org.simbrain.world.odorworld.gui.EntityNode;
import org.simbrain.world.odorworld.gui.WorldSelectionEvent;
import org.simbrain.world.odorworld.gui.WorldSelectionModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <b>OdorWorldPanel</b> represent the OdorWorld.
 */
public class OdorWorldPanel extends JPanel implements KeyListener {

    /**
     * The Piccolo PCanvas.
     */
    private final PCanvas canvas;

    /**
     * Reference to WorkspaceComponent. // TODO: Needed?
     */
    private OdorWorldComponent component;

    /**
     * Reference to model world.
     */
    private OdorWorld world;

    /**
     * Selection model.
     */
    private final WorldSelectionModel selectionModel;

    /**
     * Color of the world background.
     */
    private Color backgroundColor = Color.white;

    /**
     * The boolean that turns on and off wall drawing behavior for the mouse.
     */
    private boolean drawingWalls = false;

//    /**
//     * Point being dragged.
//     */
//    private Point draggingPoint;
//
//    /**
//     * Entity currently selected.
//     */
//    private OdorWorldEntity selectedEntity;
//
//    /**
//     * Selected point.
//     */
//    private Point selectedPoint;
//
//    /**
//     * First point for wall.
//     */
//    private Point wallPoint1;
//
//    /**
//     * Second point for wall.
//     */
//    private Point wallPoint2;
//
//    /**
//     * Distance in x direction.
//     */
//    private int distanceX;
//
//    /**
//     * Distance in y direction.
//     */
//    private int distanceY;

    /**
     * World menu.
     */
    private OdorWorldMenu menu;

    /**
     * Renderer for this world.
     */
    // private OdorWorldRenderer renderer;

    /**
     * Construct a world, set its background color.
     *
     * @param world the frame in which this world is rendered
     */
    public OdorWorldPanel(OdorWorldComponent component, OdorWorld world) {

        canvas = new PCanvas();
        setLayout(new BorderLayout());
        this.add("Center", canvas);

        canvas.setBackground(backgroundColor);
        canvas.addKeyListener(this);
        canvas.setFocusable(true);

        // Remove default event handlers
        PInputEventListener panEventHandler = canvas.getPanEventHandler();
        PInputEventListener zoomEventHandler = canvas.getZoomEventHandler();
        canvas.removeInputEventListener(panEventHandler);
        canvas.removeInputEventListener(zoomEventHandler);

        PMouseWheelZoomEventHandler zoomHandler = new PMouseWheelZoomEventHandler();
        zoomHandler.zoomAboutMouse();
        canvas.addInputEventListener(zoomHandler);

        selectionModel = new WorldSelectionModel(this);
        selectionModel.addSelectionListener((e) -> {
            updateSelectionHandles(e);
        });

        // Key events
        canvas.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK), "debug");
        canvas.getActionMap().put("debug", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                showPNodeDebugger();
            }
        });
        canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("BACK_SPACE"), "deleteSelection");
        canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DELETE"), "deleteSelection");
        canvas.getActionMap().put("deleteSelection", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                world.deleteAllEntities(); // TODO Just delete selected entities
                // world.deleteEntities(List);
            }
        });
        canvas.getInputMap().put(KeyStroke.getKeyStroke("UP"), "straight");
        canvas.getActionMap().put("straight", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                moveSelectedItem();
            }
        });
        canvas.getInputMap().put(KeyStroke.getKeyStroke("W"), "north");
        canvas.getActionMap().put("north", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if(getSelectedEntity() != null) {
                    ((EntityNode)getSelectedEntity()).getEntity().moveNorth(5);
                }
            }
        });

        canvas.addInputEventListener(new DragEventHandler2(this));

        // PCamera camera = canvas.getCamera();

        world.addPropertyChangeListener(evt -> {
            if ("entityAdded".equals(evt.getPropertyName())) {
                EntityNode node = new EntityNode(world, (OdorWorldEntity) evt.getNewValue());
                canvas.getLayer().addChild(node);
                selectionModel.setSelection(Collections.singleton(node)); // not working
            }
        });

        this.component = component;
        this.world = world;

        menu = new OdorWorldMenu(this);

        menu.initMenu();

    }

    private void moveSelectedItem() {
        if(this.getSelectedEntity() != null) {
            PNode selected = getSelectedEntity();
            selected.offset(5,0);
            // canvas.getCamera(). TODO: Do something to follow the selected agent
        }
    }

    private void showPNodeDebugger() {
        // TODO: Move to separate method attached to key command
        SceneGraphBrowser sgb = new SceneGraphBrowser(
            canvas.getRoot());
        StandardDialog dialog = new StandardDialog();
        dialog.setContentPane(sgb);
        dialog.setTitle("Piccolo Scenegraph Browser");
        dialog.setModal(false);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    // final ActionListener copyListener = new ActionListener() {
    // public void actionPerformed(ActionEvent e) {
    // WorldClipboard.copyItem(selectedEntity);
    // }
    // };
    //
    // final ActionListener cutListener = new ActionListener() {
    // public void actionPerformed(ActionEvent e) {
    // WorldClipboard.cutItem(selectedEntity, OdorWorldPanel.this);
    // //
    // getParentFrame().getWorkspaceComponent().setChangedSinceLastSave(true);
    // }
    // };
    //
    // final ActionListener pasteListener = new ActionListener() {
    // public void actionPerformed(ActionEvent e) {
    // WorldClipboard.pasteItem(selectedPoint, OdorWorldPanel.this);
    // //
    // getParentFrame().getWorkspaceComponent().setChangedSinceLastSave(true);
    // }
    // };
    //
    // final ActionListener clearAllListener = new ActionListener() {
    // public void actionPerformed(ActionEvent e) {
    // world.clearAllEntities();
    // // getParentFrame().repaint();
    // //
    // getParentFrame().getWorkspaceComponent().setChangedSinceLastSave(true);
    // }
    // };

    // final ActionListener wallListener = new ActionListener() {
    // public void actionPerformed(ActionEvent e) {
    // drawingWalls = true;
    // // getParentFrame().repaint();
    // //
    // getParentFrame().getWorkspaceComponent().setChangedSinceLastSave(true);
    // }
    // };
    //
    // final ActionListener wallPropsListener = new ActionListener() {
    // public void actionPerformed(ActionEvent e) {
    // showWallDialog((Wall) selectedEntity);
    // // getParentFrame().repaint();
    // //
    // getParentFrame().getWorkspaceComponent().setChangedSinceLastSave(true);
    // }
    // };

//    /**
//     * Handle button clicks on Odor World main panel.
//     */
//    private final MouseListener mouseListener = new MouseAdapter() {
//
//        /**
//         * Task to perform when mouse button is pressed.
//         *
//         * @param mouseEvent Mouse event
//         */
//        public void mousePressed(final MouseEvent mouseEvent) {
//
//            // Select Entity
//            selectedEntity = null;
//            selectedPoint = mouseEvent.getPoint();
//            for (OdorWorldEntity sprite : world.getObjectList()) {
//                if (sprite.getBounds().contains(selectedPoint)) {
//                    selectedEntity = sprite;
//                }
//            }
//            if (selectedEntity != null) {
//                distanceX = (int) selectedEntity.getX() - mouseEvent.getPoint().x;
//                distanceY = (int) selectedEntity.getY() - mouseEvent.getPoint().y;
//            }
//
//            // Submits point for wall drawing
//            if (drawingWalls) {
//                mouseEvent.getPoint();
//                setWallPoint1(selectedPoint);
//            }
//
//            // Show context menu for right click
//            if (mouseEvent.isControlDown() || (mouseEvent.getButton() == MouseEvent.BUTTON3)) {
//                JPopupMenu menu = getContextMenu(selectedEntity);
//                if (menu != null) {
//                    menu.show(OdorWorldPanel.this, (int) selectedPoint.getX(), (int) selectedPoint.getY());
//                }
//            }
//
//            // Handle Double clicks
//            else if (mouseEvent.getClickCount() == 2) {
//                if (selectedEntity != null) {
//                    ShowEntityDialogAction action = new ShowEntityDialogAction(selectedEntity);
//                    action.actionPerformed(null);
//                }
//            }
//
//        }
//
//        /**
//         * Task to perform when mouse button is released.
//         *
//         * @param mouseEvent Mouse event
//         */
//        public void mouseReleased(final MouseEvent mouseEvent) {
//            if (drawingWalls) {
//                setWallPoint2(mouseEvent.getPoint());
//                draggingPoint = null;
//            }
//        }
//    };

//    /**
//     * Handle mouse drags in the odor world panel.
//     */
//    private final MouseMotionListener mouseDraggedListener = new MouseMotionAdapter() {
//        /**
//         * Task to perform when mouse button is held and mouse moved.
//         *
//         * @param e Mouse event
//         */
//        public void mouseDragged(final MouseEvent e) {
//
//            if (drawingWalls) {
//                draggingPoint = e.getPoint();
//                repaint();
//            }
//
//            // Drag selected entity
//            if (selectedEntity != null) {
//
//                // Build a rectangle that corresponds to the bounds where the
//                // agent will be in the next moment. Then shrink it a bit to
//                // control the way
//                // agents "bump" into
//                // the edge of the world when dragged.
//                final Point test = new Point(e.getPoint().x + distanceX, e.getPoint().y + distanceY);
//                final Rectangle testRect = new Rectangle((int) test.getX(), (int) test.getY(), (int) selectedEntity.getWidth(), (int) selectedEntity.getHeight());
//                testRect.grow(-5, -5); // TODO: Do this shrinking in a more
//                // principled way
//
//                // Only draw change the entity location if it's in the world
//                // bounds.
//                if (getBounds().contains((testRect.getBounds()))) {
//                    selectedEntity.setX(test.x);
//                    selectedEntity.setY(test.y);
//                    repaint();
//                }
//            }
//        }
//    };

    /**
     * Task to perform when keyboard button is released.
     *
     * @param k Keyboard event.
     */
    public void keyReleased(final KeyEvent k) {
    }

    /**
     * Task to perform when keyboard button is typed.
     *
     * @param k Keyboard event.
     */
    public void keyTyped(final KeyEvent k) {
    }

    /**
     * Task to perform when keyboard button is pressed.
     *
     * @param k Keyboard event.
     */
    public void keyPressed(final KeyEvent k) {
        if (k.getKeyCode() == KeyEvent.VK_SPACE) {
            // this.fireWorldChanged();
        }

        // if (k.getKeyCode() == KeyEvent.VK_UP) {
        // world.getCurrentCreature().moveStraight();
        // } else if (k.getKeyCode() == KeyEvent.VK_DOWN) {
        // world.getCurrentCreature().goStraightBackward(1);
        // } else if (k.getKeyCode() == KeyEvent.VK_RIGHT) {
        // world.getCurrentCreature().turnRight(OdorWorld.manualMotionTurnIncrement);
        // } else if (k.getKeyCode() == KeyEvent.VK_LEFT) {
        // world.getCurrentCreature().turnLeft(OdorWorld.manualMotionTurnIncrement);
        // } else if ((k.getKeyCode() == KeyEvent.VK_DELETE) || (k.getKeyCode()
        // == KeyEvent.VK_BACK_SPACE)) {
        // world.removeEntity(selectedEntity);
        // this.getParentFrame().repaint();
        // }

        if (k.getKeyCode() != KeyEvent.VK_SPACE) {
            // this.WorldChanged();
        }

        repaint();
    }

    /**
     * passed two points, determineUpperLeft returns the upperleft point of the
     * rect. they form
     *
     * @param p1 the first point
     * @param p2 the second point
     * @return the point which is the upperleft of the rect.
     */
    private Point determineUpperLeft(final Point p1, final Point p2) {
        final Point temp = new Point();

        if (p1.x < p2.x) {
            temp.x = p1.x;
        } else if (p1.x >= p2.x) {
            temp.x = p2.x;
        }

        if (p1.y < p2.y) {
            temp.y = p1.y;
        } else if (p1.y >= p2.y) {
            temp.y = p2.y;
        }

        return temp;
    }

//    @Override
//    public void paintComponent(final Graphics g) {
//        renderer.draw((Graphics2D) g, getWorld(), this.getWidth(), this.getHeight());
//    }

    /**
     * @return The selected abstract entity.
     */
    public PNode getSelectedEntity() {
        List<PNode> selectedEntities = getSelectedEntities();
        if (!selectedEntities.isEmpty()) {
            return selectedEntities.get(0);
        }
        return null;
    }

    public List<PNode> getSelectedEntities() {
        // Assumes selected pnodes parents are entitynodes
        return getSelection().stream()
            .filter(p -> p.getParent() instanceof EntityNode)
            .map(p -> p.getParent())
            .collect(Collectors.toList());
    }

    /**
     * Create a popup menu based on location of mouse click.
     *
     * @param entity the entity for which to build the menu
     * @return the popup menu
     */
    public JPopupMenu getContextMenu(OdorWorldEntity entity) {
        JPopupMenu contextMenu = new JPopupMenu();
        // No entity was clicked on
        if (entity == null) {
            contextMenu.add(new JMenuItem(new AddEntityAction(this)));
            contextMenu.add(new JMenuItem(new AddAgentAction(this)));
            contextMenu.addSeparator();
        } else {
            contextMenu.add(menu.getCopyItem());
            contextMenu.add(menu.getCutItem());
            JMenuItem pasteItem = menu.getPasteItem();
            if (WorldClipboard.getClipboardEntity() == null) {
                pasteItem.setEnabled(false);
            }
            contextMenu.add(pasteItem);
            contextMenu.add(new JMenuItem(new ShowEntityDialogAction(entity)));
            contextMenu.add(new JMenuItem(new DeleteEntityAction(this, entity)));
            contextMenu.addSeparator();

            // TODO: Create a delete smell source action
            if (entity.getSmellSource() == null) {
                contextMenu.add(new JMenuItem(new AddSmellSourceAction(this, entity)));
                contextMenu.addSeparator();
            }


            CouplingMenu couplingMenu = new CouplingMenu(component.getWorkspace());
            couplingMenu.setSourceModel(entity);
            couplingMenu.setCustomName("Create couplings");
            contextMenu.add(couplingMenu);
            contextMenu.addSeparator();
        }
        contextMenu.add(new JMenuItem(new ShowWorldPrefsAction(this)));
        return contextMenu;
    }

    /**
     * @return Background color of world.
     */
    public int getBackgroundColor() {
        return backgroundColor.getRGB();
    }

    /**
     * Sets the background color of the world.
     *
     * @param backgroundColor Color
     */
    public void setBackgroundColor(final int backgroundColor) {
        this.backgroundColor = new Color(backgroundColor);
    }
//
//    /**
//     * @param wallPoint1 The wallPoint1 to set.
//     */
//    private void setWallPoint1(final Point wallPoint1) {
//        this.wallPoint1 = wallPoint1;
//    }
//
//    /**
//     * @return Returns the wallPoint1.
//     */
//    private Point getWallPoint1() {
//        return wallPoint1;
//    }
//
//    /**
//     * @param wallPoint2 The wallPoint2 to set.
//     */
//    private void setWallPoint2(final Point wallPoint2) {
//        this.wallPoint2 = wallPoint2;
//    }
//
//    /**
//     * @return Returns the wallPoint2.
//     */
//    private Point getWallPoint2() {
//        return wallPoint2;
//    }

    /**
     * @return the world
     */
    public OdorWorld getWorld() {
        return world;
    }

    /**
     * @param world the world to set
     */
    public void setWorld(final OdorWorld world) {
        this.world = world;
    }

    public void setBeginPosition(Point2D position) {
    }

    public void clearSelection() {
        selectionModel.clear();
    }

    public Collection<PNode> getSelection() {
        return selectionModel.getSelection();
    }

    public PCanvas getCanvas() {
        return canvas;
    }

    public void setSelection(Collection elements) {
        selectionModel.setSelection(elements);
    }

    /**
     * Last clicked position.
     */
    private Point2D lastClickedPosition;

    public Point2D getLastClickedPosition() {
        return lastClickedPosition;
    }

    public void setLastClickedPosition(Point2D position) {
        lastClickedPosition = position;
    }

    /**
     * Update selection handles.
     *
     * @param event the NetworkSelectionEvent
     */
    private void updateSelectionHandles(final WorldSelectionEvent event) {

        Set<PNode> selection = event.getSelection();
        Set<PNode> oldSelection = event.getOldSelection();

        Set<PNode> difference = new HashSet<PNode>(oldSelection);
        difference.removeAll(selection);

        for (PNode node : difference) {
            SelectionHandle.removeSelectionHandleFrom(node);
        }
        for (PNode node : selection) {
            // TODO: Move that to util class!
            SelectionHandle.addSelectionHandleTo(node);

        }
    }

    /**
     * Return true if the specified element is selected.
     *
     * @param element element
     * @return true if the specified element is selected
     */
    public boolean isSelected(final Object element) {
        return selectionModel.isSelected(element);
    }


    /**
     * Toggle the selected state of the specified element; if it is selected,
     * remove it from the selection, if it is not selected, add it to the
     * selection.
     *
     * @param element element
     */
    public void toggleSelection(final Object element) {
        if (isSelected(element)) {
            selectionModel.remove(element);
        } else {
            selectionModel.add(element);
        }
    }
}
