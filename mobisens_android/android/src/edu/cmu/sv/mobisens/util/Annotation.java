package edu.cmu.sv.mobisens.util;

import java.util.Date;

import android.graphics.Color;
import android.util.Log;

import edu.cmu.sv.lifelogger.api.LocationBasedRecognizer;
import edu.cmu.sv.lifelogger.util.DataCollector;
import edu.cmu.sv.lifelogger.util.NGramModel;
import edu.cmu.sv.mobisens.io.MobiSensLog;

public class Annotation {
	private static final String CLASS_PREFIX = Annotation.class.getName();
	
	public static final String UNKNOWN_ANNOTATION_NAME = "Unknown Activity";
	public static final String EXTRA_ANNO_START = CLASS_PREFIX + ".extra_anno_start";
	public static final String EXTRA_ANNO_END = CLASS_PREFIX + ".extra_anno_end";
	public static final String EXTRA_ANNO_NAME = CLASS_PREFIX + ".extra_anno_name";
	public static final String EXTRA_ANNO_STRING = CLASS_PREFIX + ".extra_anno_string";
	
	private Date start = null;
	private Date end = null;
	private String name = null;
	protected String guessName = "";
	
	private DataCollector<double[]> locations = new DataCollector<double[]>(-1);
	private NGramModel motionModel = null;
	private int color = Color.BLACK;
	
	private long dbId = -1;
	
	private Annotation(){}

	public Annotation(String name, Date start, Date end){
		this.setStart(start);
		this.setEnd(end);
		this.setName(name);
	}
	
	public String guessActivity(String forDeviceId){
		if(this.getLocations() == null)
			return "";
		if(!getActivityName().equals(""))
			return getActivityName();
		
		LocationBasedRecognizer classifier = new LocationBasedRecognizer(forDeviceId, this.getLocations());
		guessName = classifier.recognize();
		return guessName;
	}
	
	public String getActivityName(){
		if(getName().equals(UNKNOWN_ANNOTATION_NAME)){
			return guessName;
		}
		
		return "";  // Human annotation never show guess name.
	}
	
	public boolean hasConflict(Date start, Date end){
		if(this.getStart().before(end))
			return true;
		
		if(this.getEnd().after(start))
			return true;
		
		return false;
	}
	
	public boolean hasConflict(Annotation anno){
		if(anno == null)
			return false;
		return this.hasConflict(anno.getStart(), anno.getEnd());
	}

	
	public boolean isValid(){
		if(this.getStart().before(this.getEnd()) && this.getName().trim().equals("") == false)
			return true;
		return false;
	}

	public void setName(String name) {
		if(name == null)
			name = "";
		this.name = name.split("\\|")[0];
	}

	public String getName() {
		return name;
	}
	
	public String getEscapedName() {
		return getName().replace(",", "");
	}
	
	public static String getEscapedName(String name) {
		return name.replace(",", "");
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getStart() {
		return start;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public Date getEnd() {
		return end;
	}
	
	public String toString(){
		if(!this.isValid())
			return "";

		StringBuilder buffer = new StringBuilder(1024);
		buffer.append(this.getAnnotationPartString()).append("\t")
			.append(this.getLocationPartString()).append("\t")
			.append(this.getMotionModelPartString()).append("\t")
			.append(this.getColor());
		if(!this.getActivityName().equals("")){
			buffer.append("\t").append(this.getActivityName());
		}
		
		
		// The last field should always be the DBID.
		if(this.getDBId() != -1){
			buffer.append("\t_id:").append(this.getDBId());
		}
		
		return buffer.toString();
		
	}
	
	public static Annotation fromString(String content){
		Annotation anno = null;
		try{
			String[] annoParts = content.split("\\t");
			String[] columns = annoParts[0].split(",");
			long start = Long.valueOf(columns[0]).longValue();
			long end = Long.valueOf(columns[1]).longValue();
			String annoName = columns[2];
			boolean isMachineAnnotation = false;
			
			if(columns.length > 3){
				if(Boolean.valueOf(columns[3])){
					isMachineAnnotation = true;
				}
			}
			
			if(annoName.equals(Annotation.UNKNOWN_ANNOTATION_NAME)){ // We actually have no machine annotation any more.
				double similarity = 0.0;
				if(columns.length > 4){
					similarity = Double.valueOf(columns[4]);
				}
				anno = new MachineAnnotation(annoName, start, end, similarity);
			}else{
				anno = new Annotation(annoName, new Date(start), new Date(end));
			}
			
			if(annoParts.length > 1){
				anno.setLocations(DataCollector.fromStringWhenTIsDoubleArray(annoParts[1]));
			}
			
			if(annoParts.length > 2){
				anno.setMotionModel(NGramModel.fromString(annoParts[2]));
			}
			
			if(annoParts.length > 3){
				anno.setColor(Integer.valueOf(annoParts[3]));
			}
			
			if(annoParts.length > 4){
				if(!annoParts[4].startsWith("_id:")){
					anno.guessName = annoParts[4];
				}
			}
			
			
			// The last field should always be the DBID.
			if(annoParts[annoParts.length - 1].indexOf("_id:") != -1){
				anno.setDBId(Long.valueOf(annoParts[annoParts.length - 1].split(":")[1]));
			}
		}catch(Exception ex){
			Log.e(EXTRA_ANNO_STRING, content);
			MobiSensLog.log(ex);
			MobiSensLog.log("Invalid annotation string: " + content);
			ex.printStackTrace();
		}
		
		return anno;
	}

	public void setLocations(DataCollector<double[]> locations) {
		this.locations = locations;
	}

	public DataCollector<double[]> getLocations() {
		return locations;
	}
	
	protected String getAnnotationPartString(){
		if(!this.isValid())
			return "";

		long startTimestamp = this.getStart().getTime();
		long endTimestamp = this.getEnd().getTime();
		StringBuilder buffer = new StringBuilder(1024);
		buffer.append(startTimestamp).append(",")
			.append(endTimestamp).append(",")
			.append(this.getEscapedName()).append(",")
			.append(false);
		
		return buffer.toString();
		
	}
	
	protected String getLocationPartString(){
		if(!this.isValid())
			return "";
		
		StringBuilder buffer = new StringBuilder(1024);
		buffer.append(DataCollector.toStringWhenTIsDoubleArray(this.getLocations()));
		return buffer.toString();
	}
	
	protected String getMotionModelPartString(){
		//Log.i(this.getName(), this.getMotionModelPartString());
		if(this.getMotionModel() != null){
			return this.getMotionModel().toString();
		}
		
		return "";
	}

	public void setMotionModel(NGramModel motionModel) {
		this.motionModel = motionModel;
	}

	public NGramModel getMotionModel() {
		return motionModel;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getColor() {
		return color;
	}
	
	public void setDBId(long id){
		this.dbId = id;
	}
	
	public long getDBId(){
		return this.dbId;
	}
}
