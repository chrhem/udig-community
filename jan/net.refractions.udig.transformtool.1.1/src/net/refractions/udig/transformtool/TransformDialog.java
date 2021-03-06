/*
 *    Geotools2 - OpenSource mapping toolkit
 *    http://geotools.org
 *    (C) 2002-2006, Geotools Project Managment Committee (PMC)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.refractions.udig.transformtool;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import net.refractions.udig.project.IBlackboard;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.impl.LayerImpl;
import net.refractions.udig.project.ui.ApplicationGIS;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.geotools.referencing.operation.builder.MappedPosition;
import org.geotools.referencing.operation.builder.MathTransformBuilder;


/**
 * Dialog for transforming the layer.
 *
 * @author jezekjan
 */
public class TransformDialog extends Dialog {
    private LayerImpl sourceLayer;
    
    int i;

/**
    * Creates the dialog for transformations.
    * @param parentShell
    */
    public TransformDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Transform Tool");
    }

    protected Control createDialogArea(Composite parent) {
        /*PlatformUI.getWorkbench().getHelpSystem().setHelp(
           this.dialogArea, "org.eclipse.help.transformtoolhelp");*/

        // Label label;
        GridData gridData;
        Composite container = (Composite) super.createDialogArea(parent);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        container.setLayout(layout);
        
        final RadioGroupComposite radios = new RadioGroupComposite();
        
        IMap map = ApplicationGIS.getActiveMap();
        IBlackboard blackboard = map.getBlackboard();

        if (blackboard.get(TransformTool.BLACKBOARD_SOURCELAYER) == null) {
            sourceLayer = (LayerImpl) map.getEditManager().getSelectedLayer();
            blackboard.put(TransformTool.BLACKBOARD_SOURCELAYER, sourceLayer);
        }

        final Label label = new Label(container, SWT.SINGLE);
        label.setText("Source Layer:");
        gridData = new GridData();
        gridData.verticalSpan = 1;
        gridData.horizontalSpan = 1;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        label.setLayoutData(gridData);

        final ComboViewer comboViewer = new ComboViewer(container, SWT.SINGLE);
        comboViewer.setLabelProvider(new LayerLabelProvider());
        comboViewer.setContentProvider(new ArrayContentProvider());
        comboViewer.setInput(map.getMapLayers().toArray());

        comboViewer.setSelection((ISelection) (new StructuredSelection(
                (Layer) blackboard.get(TransformTool.BLACKBOARD_SOURCELAYER))),
            true);

        gridData = new GridData();
        gridData.verticalSpan = 2;
        gridData.horizontalSpan = 2;
        gridData.verticalAlignment = GridData.FILL;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        comboViewer.getCombo().setLayoutData(gridData);

        comboViewer.addSelectionChangedListener(new ISelectionChangedListener() {
                public void selectionChanged(SelectionChangedEvent event) {
                    IStructuredSelection selection = (IStructuredSelection) event
                        .getSelection();
                    IMap map = ApplicationGIS.getActiveMap();
                    IBlackboard blackboard = map.getBlackboard();                   
                    blackboard.put(TransformTool.BLACKBOARD_SOURCELAYER,
                        (LayerImpl) selection.getFirstElement());

                    System.out.println("Selected: "
                        + selection.getFirstElement());
                    sourceLayer = (LayerImpl) selection.getFirstElement();
                    radios.refresh();
                }
            });

        //*******************Vector Group*************   
       

        gridData = new GridData();        
        gridData.horizontalSpan = 2;   
       
        gridData.verticalSpan = 1;
   

        (new VectorToolComposite(container)).addWidgets(gridData, radios);
        //      *******************Radio Group************* 
        gridData = new GridData();        
        gridData.horizontalSpan = 2;
        gridData.grabExcessHorizontalSpace=true;
        gridData.verticalSpan = 3;
        gridData.horizontalAlignment = GridData.FILL;
        radios.addRadios(container, gridData);

        //**********************Info and transform group*****     
        Button infoButton = new Button(container, SWT.BUTTON1);
        infoButton.setText("Transformation info");

        Button transButton = new Button(container, SWT.BUTTON1);
        transButton.setText("Transform");
        gridData = new GridData();
        gridData.verticalSpan = 1;
        gridData.horizontalSpan = 2;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        infoButton.setLayoutData(gridData);
        transButton.setLayoutData(gridData);

        transButton.setEnabled(true);

        transButton.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event) {
                    (new DialogUtility()).transClick();
                    getShell().close();
                }
            });
        infoButton.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event) {
                    Display.getDefault().syncExec(new Runnable() {
                            public void run() {
                                IMap map = ApplicationGIS.getActiveMap();
                                IBlackboard blackboard = map.getBlackboard();
                                                               

                                Object mt =  blackboard
                                    .get(TransformTool.BLACKBOARD_MATHTRANSFORM);
                                MathTransformBuilder calculator = (MathTransformBuilder) blackboard
                                    .get(TransformTool.BLACKBOARD_CALCULATOR);
                                int number = ((List<MappedPosition>) blackboard.get(VectorLayerReader.BLACKBOARD_PTS)).size();

                                //    Layer sourceLayer = (Layer) blackboard.get(TransformTool.BLACKBOARD_SOURCELAYER);
                                //CoordinateReferenceSystem c = sourceLayer.getCRS();
                                try {
                                    double m = calculator.getErrorStatistics().rms();
                                   
                                   NumberFormat foramter = new DecimalFormat("0.00E0");
                                   String error = foramter.format(m);
                                    //m = Math.round(m*1000)/1000 ;
                                    String info = "Transformation summary"
                                        + "\n" + "\n"
                                        + "Number of ground control points: "
                                        + number + "\n" + "\n"
                                        + "Cartesian  Standard deviation = " + error + " " +  
                                        ((Layer)blackboard.get(TransformTool.BLACKBOARD_SOURCELAYER)).getCRS().getCoordinateSystem().getAxis(0).getUnit()
                                        + "\n" + "\n" + mt.toString() + "\n" 
                                        
                                       ;

                                    Dialog dialog = new InfoDialog(Display.getDefault()
                                                                          .getActiveShell(),
                                            info);
                                    dialog.open();

                                    /*       MessageDialog.openInformation(Display.getDefault()
                                       .getActiveShell(),
                                       "Transformation summary",
                                       "Number of idetical points: "
                                       + ptSrc.length + "\n" + "\n"
                                       + "Standard deviation = " + m +"\n" + "\n" + "\n"
                                       + mt.toWKT()+"\n"+
                                       "SourceLayer coordiante System"+ sourceLayer);
                                     */
                                } catch (Exception e) {
                                    MessageDialog.openInformation(Display.getDefault()
                                                                         .getActiveShell(),
                                        "Transformation summary", e.toString());  
                                }    
                            }
                        });
                }
            });

        return container;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.CANCEL_ID,
            IDialogConstants.CANCEL_LABEL, false);
    }

    
}
