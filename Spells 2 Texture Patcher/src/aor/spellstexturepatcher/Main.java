package aor.spellstexturepatcher;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

public class Main {
	public static boolean nogui;
	public static ArrayList<String> zips= new ArrayList<String>();
	private static BufferedImage scepter;
	private static File workingDirectory;
	private static String directoryName;
	public static void main(String[] arguments) {
		List<String> args=Arrays.asList(arguments);
		for(String arg:args){
			if(arg.equalsIgnoreCase("nogui"))nogui=true;
			if(arg.endsWith(".zip")||arg.endsWith("minecraft.jar"))zips.add(arg);
		}
		directoryName="temp/";
		Random r=new Random();
		int i=0;
		while(new File(directoryName).exists()){
			directoryName="temp"+r.nextLong()+"/";
			if(i>1000)return;
			else i++;
		}
		workingDirectory=new File(directoryName);
		workingDirectory.mkdir();
		//workingDirectory.deleteOnExit();
		System.out.println("directory created!");
		File stp=new File("stp.jar");
		if(!stp.exists())return;
		File imageFile=new File(workingDirectory.getPath()+"/Spells.png");
		try {
			ZipInputStream zis=new ZipInputStream(new BufferedInputStream(new FileInputStream(stp),2048));
			ZipEntry entry;
			while((entry=zis.getNextEntry()) != null){
				if(entry.getName().equals("Staff.png")){
					byte[] data=new byte[2048];
					int amount;
					BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(imageFile),2048);
					while((amount=zis.read(data,0,2048))!=-1){
						bos.write(data,0,amount);
					}
					bos.flush();
					bos.close();
				}
			}
			scepter=ImageIO.read(imageFile);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		imageFile.delete();
		System.out.println(zips);
		for(String file:zips){
			File zip=new File(file);
			if(zip.exists()){
				System.out.println(zip.getName()+" exists!");
				if(file.endsWith("minecraft.jar"))createPackFromJar(zip);
				else modifyPack(zip);
			}
			//workingDirectory.delete();
			//workingDirectory.mkdir();
		}
		//workingDirectory.delete();
	}
	private static void modifyPack(final File zip){
		try{
			ZipInputStream zis=new ZipInputStream(new BufferedInputStream(new FileInputStream(zip),2048));
			ZipEntry entry;
			while((entry=zis.getNextEntry()) != null){
				System.out.println(entry.getName());
				byte[] data=new byte[2048];
				int amount;
				File entryFile=new File(directoryName+entry.getName());
				File entryDirectory=new File(directoryName+entry.getName().substring(0, entry.getName().lastIndexOf('.')==-1?entry.getName().lastIndexOf('.'):entry.getName().length()));
				System.out.println(entryDirectory.mkdirs());
				System.out.println(entryFile.createNewFile());
				BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(entryFile),2048);
				while((amount=zis.read(data,0,2048))!=-1){
					bos.write(data,0,amount);
				}
				bos.flush();
				bos.close();
			}
			BufferedImage items=ImageIO.read(new File(directoryName+"gui/items.png"));
			Graphics2D g=((Graphics2D)items.getGraphics());
			g.drawImage(scepter,64,128,null);
			ImageIO.write(items, "png", new File(directoryName+"gui/items.png"));
			ZipOutputStream zos=new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(new File(zip.getAbsolutePath().substring(0, zip.getAbsolutePath().length()-4)+" (Spells).zip"))));
			for(File file:getIterator(workingDirectory)){
				entry=new ZipEntry(file.getPath().replaceFirst(directoryName, ""));
				zos.putNextEntry(entry);
				BufferedInputStream bis=new BufferedInputStream(new FileInputStream(file));
				byte[] data=new byte[2048];
				int amount;
				while((amount=bis.read(data, 0, 2048))!=-1){
					zos.write(data, 0, amount);
				}
				zos.closeEntry();
			}
			zos.flush();
			zos.close();
		} catch(Exception e){}
	}
	private static void createPackFromJar(final File jar){
		try{
			ZipInputStream zis=new ZipInputStream(new BufferedInputStream(new FileInputStream(jar),2048));
			ZipEntry entry;
			while((entry=zis.getNextEntry()) != null){
				if(!entry.getName().endsWith(".class")){
					byte[] data=new byte[2048];
					int amount;
					BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(new File(directoryName+entry.getName())),2048);
					while((amount=zis.read(data,0,2048))!=-1){
						bos.write(data,0,amount);
					}
					bos.flush();
					bos.close();
				}
			}
			BufferedImage items=ImageIO.read(new File(directoryName+"gui/items.png"));
			Graphics2D g=((Graphics2D)items.getGraphics());
			g.drawImage(scepter,64,128,null);
			ImageIO.write(items, "png", new File(directoryName+"gui/items.png"));
			ZipOutputStream zos=new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(new File(jar.getAbsolutePath().substring(0, jar.getAbsolutePath().length()-13)+"Default Texture Pack (Spells).zip"))));
			for(File file:getIterator(workingDirectory)){
				entry=new ZipEntry(file.getPath().replaceFirst(directoryName, ""));
				zos.putNextEntry(entry);
				BufferedInputStream bis=new BufferedInputStream(new FileInputStream(file));
				byte[] data=new byte[2048];
				int amount;
				while((amount=bis.read(data, 0, 2048))!=-1){
					zos.write(data, 0, amount);
				}
				zos.closeEntry();
			}
			zos.flush();
			zos.close();
		} catch(Exception e){}
	}
	private static Iterable<File> getIterator(final File directory){
		return new Iterable<File>(){
			@Override
			public Iterator<File> iterator() {
				return new Iterator<File>(){
					private File dir=directory;
					private int current=0;
					private Iterator<File> currentIterator=null;
					@Override
					public boolean hasNext() {
						if(currentIterator!=null){
							if(currentIterator.hasNext())return true;
							else currentIterator=null;
							current++;
						}
						if(!dir.isDirectory()){
							if(current==0)return true;
							else return false;
						}
						else if(dir.listFiles().length==current)return false;
						return true;
					}

					@Override
					public File next() {
						if(currentIterator!=null)return currentIterator.next();
						current++;
						File f=dir.listFiles()[current-1];
						if(f.isDirectory()){
							currentIterator=getIterator(f).iterator();
							if(currentIterator.hasNext())return currentIterator.next();
							else if(hasNext())return next();
							else return null;
						}
						return f;
					}
					@Override
					public void remove() {}
				};
			}
			
		};
	}
}