package com.hamburgsud.FileUnziped.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

@Service
public class FileUnzipedService {

	@Value("${app.incomingFolder}")
	private String incomingfolder;
	@Value("${app.needBackup}")
	private boolean needBackup;
	@Value("${app.backupFolder}")
	private String backupFolder;
	@Value("${app.folderUnziped}")
	private String folderUnziped;
	
	public void Unziped() throws Exception{
		
		File incoming = new File(incomingfolder);
		
		if (!incoming.isDirectory())
			throw new Exception("não é diretório" );
		
		
		File[] listFiles = incoming.listFiles();
		for (File file : listFiles) {
			String namePath = FilenameUtils.getBaseName(file.getName());
			//namePath = namePath.substring(namePath.indexOf("_") +1);
			String UnzipiDestination = folderUnziped + namePath;
		  
			
			
			
			if(FilenameUtils.getExtension(file.getName()).contains("zip")) {
			    CriaPasta(UnzipiDestination);
			    String fileZip = file.getAbsolutePath() ;
		        File destDir = new File(UnzipiDestination);
		        byte[] buffer = new byte[1024];
		        ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
		        ZipEntry zipEntry = zis.getNextEntry();
		        while (zipEntry != null) {
		        	if (zipEntry.isDirectory()) {
		        		String NewDiretorio = destDir.getPath()+"\\"+zipEntry.getName();
		        		CriaPasta(NewDiretorio);
		        	}else{
			            File newFile = newFile(destDir, zipEntry);
			            FileOutputStream fos = new FileOutputStream(newFile);
			            
			            int len;
			            while ((len = zis.read(buffer)) > 0) {
			                fos.write(buffer, 0, len);
			            }
			            fos.close();
		        	}   
		            zipEntry = zis.getNextEntry();
		        }
		        zis.closeEntry();
		        zis.close();
			   if (needBackup) {
				   BackUp(file); 
			   }
			   file.delete();
			   
		   }else {
		   
			   if (FilenameUtils.getExtension(file.getName()).contains("7z") ) {
				   
			    CriaPasta(UnzipiDestination);
				SevenZFile sevenZFile = new SevenZFile(file);
		        SevenZArchiveEntry entry;
		       
		        
		        while ((entry = sevenZFile.getNextEntry()) != null){
		            if (entry.isDirectory()){
		                continue;
		            }
		            File curfile = new File(UnzipiDestination, entry.getName());
		            if (!curfile.exists()) {
			            File parent = curfile.getParentFile();
			            if (!parent.exists()) {
			                parent.mkdirs();
			            }
			            FileOutputStream out = new FileOutputStream(curfile);
			            byte[] content = new byte[(int) entry.getSize()];
			            sevenZFile.read(content, 0, content.length);
			            out.write(content);
			            out.close();
		            }
		        }
		        if (needBackup) {
				   BackUp(file); 
		        }
		        file.delete();
		        
		   }
		  }
			   
		
    }
     
	        
		
	}

	private void BackUp(File file) throws IOException {		
		File backupfile = Paths.get(backupFolder + file.getName()).toFile();
		FileCopyUtils.copy(file, backupfile);
	}

	private void CriaPasta(String UnzipiDestination) {
		File pathDestination = new File(UnzipiDestination);
		if (!pathDestination.exists()) pathDestination.mkdirs();
	}
	
	 public static File newFile(File destinationDir, ZipEntry zipEntry) throws Exception {
	        File destFile = new File(destinationDir, zipEntry.getName());
	         
	        String destDirPath = destinationDir.getCanonicalPath();
	        String destFilePath = destFile.getCanonicalPath();
	         
	        if (!destFilePath.startsWith(destDirPath + File.separator)) {
	            throw new Exception("Entry is outside of the target dir: " + zipEntry.getName());
	        }
	         
	        return destFile;
	    }
}
