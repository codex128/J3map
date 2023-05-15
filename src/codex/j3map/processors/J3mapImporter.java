/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.j3map.processors;

import codex.j3map.J3map;
import com.jme3.asset.AssetManager;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gary
 */
public class J3mapImporter implements J3mapPropertyProcessor<J3map> {
	
	public static final String META_PROPERTY = "J3mapImporter[map_source](API-Defined)";
	private static final Logger LOG = Logger.getLogger(J3mapImporter.class.getName());
	
	private AssetManager assetManager;
	
	public J3mapImporter() {}
	public J3mapImporter(AssetManager assetManager) {
		this.assetManager = assetManager;
	}
	
	@Override
	public Class<J3map> type() {
		return J3map.class;
	}
	@Override
	public J3map process(String str) {
		if (!str.startsWith(getPropertyIdentifier()+"(\"") || !str.endsWith("\")")) {
			return null;
		}
		String source = str.substring(getPropertyIdentifier().length()+2, str.length()-2);
		try {
			J3map map;
			if (assetManager != null) {
				map = (J3map)assetManager.loadAsset(source);
			}
			else {
				map = new J3map(new FileInputStream(source));
			}
			map.overwrite(META_PROPERTY, source);
			return map;
		} catch (IOException ex) {
			LOG.log(Level.SEVERE, "An error occured while importing a J3map", ex);
		}
		return null;
	}
	@Override
	public String[] export(J3map property) {
		String source = property.getString(META_PROPERTY);
		if (source == null) {
			LOG.log(Level.SEVERE, "Metadata lost, failed to retrieve source path!");
			return null;
		}
		return new String[]{getPropertyIdentifier()+"(\""+source+"\")"};
	}
	@Override
	public String getPropertyIdentifier() {
		return "import";
	}
	@Override
	public J3map[] createArray(int length) {
		return new J3map[length];
	}
	
}
