package edu.cmu.sv.mobisens.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.zip.GZIPOutputStream;

import android.content.Context;

import edu.cmu.sv.mobisens.MobiSensService;
import edu.cmu.sv.mobisens.SensorService;
import edu.cmu.sv.mobisens.util.Annotation;
import edu.cmu.sv.mobisens.util.MachineAnnotation;

public class FileOperation {
	public static boolean copyFile(String sourceFile, String destFile, boolean deleteAfterCopied){
		try{
			  File f1 = new File(sourceFile);
			  File f2 = new File(destFile);
			  
			  if(!f1.exists())
				  return false;
			  
			  InputStream in = new BufferedInputStream(new FileInputStream(f1));
			  
			  //For Overwrite the file.
			  OutputStream out = new BufferedOutputStream(new FileOutputStream(f2), 100 * 1024);
			
			  byte[] buf = new byte[500 * 1024];
			  int len;
			  while ((len = in.read(buf)) > 0){
				  out.write(buf, 0, len);
			  }
			  in.close();
			  out.close();

			  if(deleteAfterCopied){
				  f1.delete();
			  }
			  
			  return true;
		  }catch(Exception ex){
			  ex.printStackTrace();
			  MobiSensLog.log(ex);
		  }
		  
		  return false;
	}
	
	public static String[] getFilesInDirectory(String directoryPath){
		File directory = new File(directoryPath);
		if(!directory.isDirectory())
			return new String[0];
		File[] files = directory.listFiles();
		String[] result = new String[files.length];
		
		int index = 0;
		for(File file:files){
			result[index] = file.getAbsolutePath();
			index++;
		}
		
		return result;
	}
	
	public synchronized static String readFileAsString(File file){
		if(!file.exists())
			return "";
		
        StringBuffer fileData = new StringBuffer(1000);
        try{
        	BufferedReader reader = new BufferedReader(new FileReader(file));
            char[] buf = new char[1024];
            int numRead=0;
            while((numRead=reader.read(buf)) != -1){
                String readData = String.valueOf(buf, 0, numRead);
                fileData.append(readData);
                buf = new char[1024];
            }
            reader.close();
        }catch(Exception ex){
        	MobiSensLog.log(ex);
        }
        
        return fileData.toString();
    }
	
	public synchronized static boolean writeStringToFile(File file, String content){
		try{
			FileWriter fileWritter = new FileWriter(file, false);
	        BufferedWriter bufferWritter = new BufferedWriter(fileWritter, 100 * 1024);
	        
	        bufferWritter.write(content);
	        bufferWritter.close();
		}catch(Exception ex){
			MobiSensLog.log(ex);
			return false;
		}
		
		return true;
	}
	
	public synchronized static boolean appendStringToFile(File file, String content){
		try{
			FileWriter fileWritter = new FileWriter(file, true);
	        BufferedWriter bufferWritter = new BufferedWriter(fileWritter, 100 * 1024);
	        
	        bufferWritter.write(content);
	        bufferWritter.close();
		}catch(Exception ex){
			MobiSensLog.log(ex);
			return false;
		}
		
		return true;
	}
	
	public synchronized static boolean closeRandomAccessFile(RandomAccessFile file){
		try {
			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			MobiSensLog.log(e);
			return false;
		}
		
		return true;
	}
	
	public static ArrayList<String> getLastNLines(File file, int n){
		
		ArrayList<String> result = new ArrayList<String>();
		
		if(!file.exists()){
			return result;
		}

		RandomAccessFile randomAccessFile = null;
		
		try {
			randomAccessFile = new RandomAccessFile(file, "r");
			if(randomAccessFile.length() == 0){
				return result;
			}
			
			// Go to the end of the file
			long pos = randomAccessFile.length() - 1;
			randomAccessFile.seek(pos);
			int lineRead = 0;
			while((lineRead < n || n < 0) && pos >= 0){
				try{
					byte chr = randomAccessFile.readByte();
					if(chr == '\n' || pos == 0){
						if(pos == 0){
							// The current pos is 1 because of the readByte.
							randomAccessFile.seek(pos);
						}
						String line = randomAccessFile.readLine();
						if(line != null){
							result.add(0, line);
							lineRead++;
						}
					}
				} catch (IOException ioex){
					ioex.printStackTrace();
				}
				
				if(pos > 0){
					pos--;
					randomAccessFile.seek(pos);
				}else{
					break;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(randomAccessFile != null){
			try {
				randomAccessFile.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	
	public synchronized static boolean deleteFile(String filePath){
		File file = new File(filePath);
		if(file.exists()){
			return file.delete();
		}
		
		return false;
	}
	
	
	public static File GzipCompress(File input){
		
        BufferedWriter bufferedWriter = null;
        BufferedReader bufferedReader = null;
        File returnFile = input;
        
        
        try {
            File outputFile = Directory.openFile(Directory.MOBISENS_ROOT, input.getName() + ".gz");
            
            //Construct the BufferedWriter object
            bufferedWriter = new BufferedWriter(
                                 new OutputStreamWriter(
                                     new GZIPOutputStream(new FileOutputStream(outputFile))
                                 ));

            //Construct the BufferedReader object
            bufferedReader = new BufferedReader(new FileReader(input));
            char[] buffer = new char[1024 * 8]; // 8K buffer;
           
            // from the input file to the GZIP output file
            int numRead = 0;
			while ((numRead = bufferedReader.read(buffer)) != -1) {
                bufferedWriter.write(buffer, 0, numRead);
            }
			
			returnFile = outputFile;
           
        } catch (Exception e) {
            e.printStackTrace();
            MobiSensLog.log(e);
        } finally {
            //Close the BufferedWrter
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    MobiSensLog.log(e);
                }
            }
           
            //Close the BufferedReader
            if (bufferedReader != null ){
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    MobiSensLog.log(e);
                }
            }
        }
        
        return returnFile;

	}
}
