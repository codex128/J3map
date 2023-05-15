# J3map
File format similar to JSON for JMonkeyEngine3.

# Download
1. Download the appropriate jar files.
2. Add the files to the "libraries" of your project.

# Usage
Initialize J3map parsing.
```
// register J3mapFactory as an asset loader
assetManager.registerLoader(J3mapFactory.class, "j3map");
// register property processors, which help J3mapFactory parse individual properties
J3mapFactory.registerAllProcessors(
    BooleanProcessor.class,
    StringProcessor.class,
    FloatProcessor.class
    /* etc... */);
```
Load and read a j3map file.
```
J3map file = (J3map)assetManager.loadAsset("MyFile.j3map");
boolean a = file.getBoolean("myBoolean", true); // fetch boolean property named "myBoolean", if not found, return true by default.
String str = file.getString("myString"); // fetch String property named "myString", returns null by default.
MyClass c = file.getProperty(MyClass.class, "somedata"); // fetch MyClass property named "somedata", returns null by default.
```
Write to and save a j3map.
```
J3map file = ...;
file.store("myInteger", 5); // stores the int 5 at "myInteger", unless "myInteger" already exists.
file.overwrite("myFloat", 0.25f); // stores the float 0.25f at "myFloat", if "myFloat" already exists, this method will overwrite it.
try {
  // saves all the data in "MyFile.j3map" in user1's home folder. If "MyFile.j3map" already exists there, then it will be overwritten.
  file.export("/home/user1/MyFile.j3map");
}
catch (IOException ex) { /* ... */ }
```
# File Syntax
A typical j3map file looks like this:
```
myString: "hello world";
myBoolean: false;
myInteger: 5;
myMap: {
  myFloat: 0.25f;
}
```
Spaces and tabs are optional. Number type must be easily identifiable, for instance, all float numbers must end with 'f', otherwise the number might be interpreted as an integer.

Notice `myFloat` is enclosed between a set of curly-braces labeled as `myMap`. `myMap` *is* a J3map, stored inside another J3map.
This is how `myFloat` is accessed:
```
J3map file = ...;
J3map myMap = file.getJ3map("myMap");
Float f = myMap.getFloat("myFloat");
```
# Property Processors
J3map parses individual properties using the `J3mapPropertyProcessor` interface. In order for J3map to properly process a property type, the corresponding processor must be registered with `J3mapFactory`. The J3map library comes default with several processors:
* BooleanProcessor
* StringProcessor
* FloatProcessor
* IntegerProcessor
* LongProcessor
* J3mapImporter (imports external j3map files, syntax: `import("myFile.j3map")`)
* ArrayProcessor (experimental, may not work as expected)
* StringArrayProcessor (deprecated)
The syntax for these processors is the same as Java syntax (with the exception of StringArrayProcessor, which uses square-brackets instead of curly-braces).

# Additional Notes on `J3mapImporter`
Functionality for `J3mapImporter` is, admittably, somewhat limited. J3mapImporter can optionally take an `AssetManager` as a constructor argument. If an `AssetManager` is supplied, `J3mapImporter` will look in the project assets (using the asset manager, which is fail-on-miss) for the imported j3map. Otherwise, `J3mapImporter` will look in the user's files (using `File`). *There is currently no way to look in both places in one project*.
