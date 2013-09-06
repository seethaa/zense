package edu.cmu.sv.mobisens.io;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;

import android.util.Log;
import edu.cmu.sv.mobisens.audioprocessing.FFT2;
import edu.cmu.sv.mobisens.audioprocessing.HammingWindow;
import edu.cmu.sv.mobisens.audioprocessing.MFCC2;
import edu.cmu.sv.mobisens.util.KeyValuePair;

public class AudioProcessor extends AudioRecorder {
	
	private static final int FFT_SIZE = 8192;
    private static final int MFCCS_VALUE = 12;
    private static final int MEL_BANDS = 20;

	private int bufferIndex = 0;
	private double[] buffer = null;
	//private MFCC mfcc = null;
	
	private MFCC2 mfcc = null;
	private FFT2 fft = new FFT2(FFT_SIZE);
	private HammingWindow featureWin = null;
	
	private ArrayList<KeyValuePair<Long, short[]>> samples = new ArrayList<KeyValuePair<Long, short[]>>(30);

	
	private double rmsThreadshold = 9;
	private int pickedSampleIndex = 0;
	private int frameIndex = 0;
	
	public final int SAMPLE_PER_SECOND = 2;
	

	public AudioProcessor(){
		this(9);
		
		
	}
	
	public AudioProcessor(double rmsThreshold){
		super();
		
		this.rmsThreadshold = rmsThreshold;
		this.pickedSampleIndex = AudioRecorder.SAMPLING_FREQUENCY / (this.getBufferSizeInBytes() / 2) / SAMPLE_PER_SECOND - 1;
		
		/*
		int nnumberofFilters = 24;
		int nlifteringCoefficient = 22;
		boolean oisLifteringEnabled = true;
		boolean oisZeroThCepstralCoefficientCalculated = false;
		int nnumberOfMFCCParameters = 12; //without considering 0-th
		double dsamplingFrequency = (double)AudioRecorder.SAMPLING_FREQUENCY;
		
		int nFFTLength = 512;  // 8000Hz * 0.064s
		
		if (oisZeroThCepstralCoefficientCalculated) {
		  //take in account the zero-th MFCC
		  nnumberOfMFCCParameters = nnumberOfMFCCParameters + 1;
		}
		else {
		  nnumberOfMFCCParameters = nnumberOfMFCCParameters;
		}
		*/
		
		/*
		this.mfcc = new MFCC(nnumberOfMFCCParameters,
		                     dsamplingFrequency,
		                     nnumberofFilters,
		                     nFFTLength,
		                     oisLifteringEnabled,
		                     nlifteringCoefficient,
		                     oisZeroThCepstralCoefficientCalculated);
		                     */
		
		/*
		this.mfcc = new MFCC2(FFT_SIZE, MFCCS_VALUE, 
				MEL_BANDS, (double)AudioRecorder.SAMPLING_FREQUENCY);
				*/
	}
	
	protected void dataArrival(long timestamp, short[] data, int length, int frameLength){
		
		if(this.frameIndex % this.pickedSampleIndex == 1){
			
			synchronized(samples){
				samples.add(new KeyValuePair<Long, short[]>(Long.valueOf(timestamp), data));
			}
			//Log.i("Frame", "Frame added");
			
		}
		
		frameIndex++;
		if(frameIndex == Integer.MAX_VALUE - 1)
			this.frameIndex = 0;
		//Log.i("Frame", "Frame: " + frameIndex);
	}
	
	protected void onFrameCompleted(long timestamp, double[] buffer, double RMS){
		double[] features = null;
		//if(RMS >= this.getRMSThreshold()){
			//features = mfcc.getParameters(buffer);
		//}
		
		double fftBufferR[] = new double[FFT_SIZE];
        double fftBufferI[] = new double[FFT_SIZE];
        double featureCepstrum[] = new double[MFCCS_VALUE];
        featureWin = new HammingWindow(buffer.length);
        
        for (int i = 0; i < buffer.length; i++)
        {
        	if(i > fftBufferR.length - 1)
        		break;
            fftBufferR[i] = buffer[i];
        }

        featureWin.applyWindow(fftBufferR);
        fft.fft(fftBufferR, fftBufferI);
        
        featureCepstrum = this.mfcc.cepstrum(fftBufferR, fftBufferI);
		
        onMFCCFeatures(timestamp, featureCepstrum, RMS);
        
	}
	
	protected void onMFCCFeatures(long timestamp, double[] mfccFeatures, double RMS){
		
	}
	
	public double getRMSThreshold(){
		return this.rmsThreadshold;
	}
	
	public ArrayList<KeyValuePair<Long, short[]>> getSamples(){
		synchronized(samples){
			return new ArrayList<KeyValuePair<Long, short[]>>(this.samples);
		}
	}
}
