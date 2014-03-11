package edu.cmu.sv.lifelogger.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import edu.cmu.sv.mobisens.content.AnnotationWidget;
import edu.cmu.sv.mobisens.util.Annotation;

public class ReceiverService extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		
		Bundle extras = intent.getExtras();
		String annotationString = (String) extras.get(Annotation.EXTRA_ANNO_STRING);
		Annotation anno = Annotation.fromString(annotationString);
		Boolean mergeWithLastAnno = extras.getBoolean(AnnotationWidget.EXTRA_MERGE_WITH_LAST_ANNO);
		
		
		
		System.out.println("Here" + intent.getAction());

	}
}