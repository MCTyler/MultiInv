package uk.co.tggl.pluckerpluck.multiinv.books;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import uk.co.tggl.pluckerpluck.multiinv.MIYamlFiles;
import uk.co.tggl.pluckerpluck.multiinv.MultiInv;

/**
 * This class holds all the pages of a book, including author name and title.
 * 
 * @author joshua
 * 
 */
public class MIBook {
    
    String hashcode = "";
    String author = "";
    String title = "";
    String[] pages = new String[0];
    YamlConfiguration ybookfile = new YamlConfiguration();;
    File file;
    private File dataFolder = Bukkit.getServer().getPluginManager().getPlugin("MultiInv").getDataFolder();
    
    public MIBook(String hashcode, String author, String title, String[] pages) {
        this.hashcode = hashcode;
        this.author = author;
        this.title = title;
        this.pages = pages;
        if(MIYamlFiles.usesql) {
            // If we are using mySQL let's assume we are getting this loaded from the mySQL connector...
            // So we shouldn't have to do anything. :D (There should be no other way this is called anyways.)
        } else {
            file = new File(dataFolder.getAbsolutePath() + File.separator + "books" + File.separator + "book_" + hashcode + ".yml");
            if(file.exists()) {
                ybookfile = new YamlConfiguration();
                load();
            } else {
                save();
            }
        }
    }
    
    @SuppressWarnings("LeakingThisInConstructor")
    public MIBook(String author, String title, String[] pages) {
        String allpages = title + ";author;" + author;
        for (String page : pages) {
            allpages = allpages + ";newpage;" + page;
        }
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] hashbytes = md5.digest(allpages.getBytes("UTF-8"));
            BigInteger bigInt = new BigInteger(1, hashbytes);
            hashcode = bigInt.toString(16);
            this.author = author;
            this.title = title;
            this.pages = pages;
            if(MIYamlFiles.usesql) {
                MIBook newbook = MIYamlFiles.con.getBook(hashcode, false);
                if(newbook == null) {
                    MIYamlFiles.con.saveBook(this, false);
                } else {
                    // We don't need to do anything if there is a result.
                    // It should all be exactly the same.
                }
            } else {
                file = new File(dataFolder.getAbsolutePath() + File.separator + "books" + File.separator + "book_" + hashcode + ".yml");
                if(file.exists()) {
                    ybookfile = new YamlConfiguration();
                    load();
                } else {
                    save();
                }
            }
        } catch(NoSuchAlgorithmException e) {
        } catch(UnsupportedEncodingException e) {
        }
    }
    
    @SuppressWarnings("LeakingThisInConstructor")
    public MIBook(String author, String title, List<String> pages) {
        String allpages = title + ";author;" + author;
        String[] spages = new String[pages.size()];
        for(int i = 0; i < pages.size(); i++) {
            allpages = allpages + ";newpage;" + pages.get(i);
            spages[i] = pages.get(i);
        }
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] hashbytes = md5.digest(allpages.getBytes("UTF-8"));
            BigInteger bigInt = new BigInteger(1, hashbytes);
            hashcode = bigInt.toString(16);
            this.author = author;
            this.title = title;
            this.pages = spages;
            if(MIYamlFiles.usesql) {
                MIBook newbook = MIYamlFiles.con.getBook(hashcode, false);
                if(newbook == null) {
                    MIYamlFiles.con.saveBook(this, false);
                } else {
                    // We don't need to do anything if there is a result.
                    // It should all be exactly the same.
                }
            } else {
                file = new File(dataFolder.getAbsolutePath() + File.separator + "books" + File.separator + "book_" + hashcode + ".yml");
                if(file.exists()) {
                    ybookfile = new YamlConfiguration();
                    load();
                } else {
                    save();
                }
            }
        } catch(NoSuchAlgorithmException e) {
        } catch(UnsupportedEncodingException e) {
        }
    }
    
    public MIBook(String hashcode) {
        this(new File(Bukkit.getServer().getPluginManager().getPlugin("MultiInv").getDataFolder() + File.separator +
                "books" + File.separator + "book_" + hashcode + ".yml"));
    }
    
    public MIBook(File bookfile) {
        file = bookfile;
        if(bookfile.exists()) {
            ybookfile = new YamlConfiguration();
            load();
        }
        String allpages = title + ";author;" + author;
        for (String page : pages) {
            allpages = allpages + ";newpage;" + page;
        }
        MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
	        byte[] hashbytes = md5.digest(allpages.getBytes("UTF-8"));
	        BigInteger bigInt = new BigInteger(1, hashbytes);
	        hashcode = bigInt.toString(16);
		} catch (NoSuchAlgorithmException e) {
		} catch (UnsupportedEncodingException e) {
		}
        
    }
    
    public String getHashcode() {
        return hashcode;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String[] getPages() {
        return pages;
    }
    
    private void load() {
        if(file.exists()) {
            try {
                ybookfile.load(file);
                author = ybookfile.getString("author", "");
                List<String> spages = ybookfile.getStringList("pages");
                pages = new String[spages.size()];
                for(int i = 0; i < pages.length; i++) {
                    pages[i] = spages.get(i);
                    MultiInv.log.debug("Loaded page " + i + " of book.");
                    MultiInv.log.debug("contents: " + pages[i]);
                }
                title = ybookfile.getString("title", "");
            } catch(IOException e) {
            } catch (InvalidConfigurationException e) {
            }
        } else {
            save();
        }
    }
    
    private void save() {
        ArrayList<String> pagearray = new ArrayList<String>();
        pagearray.addAll(Arrays.asList(pages));
        ybookfile.set("pages", pagearray);
        ybookfile.set("author", author);
        ybookfile.set("title", title);
        try {
            ybookfile.save(file);
        } catch(Exception e) {
        }
    }
    
}
