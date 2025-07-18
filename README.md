# Generating an HTML page that references or includes various files
[![Maven Central](https://img.shields.io/maven-central/v/io.github.vdaburon/create-html-for-files-in-directory)](https://central.sonatype.com/artifact/io.github.vdaburon/create-html-for-files-in-directory)
[![Apache License](http://img.shields.io/badge/license-Apache-green.svg)](https://github.com/vdaburon/CreateHtmlForFilesInDirectory/blob/main/LICENSE)
![Free](https://img.shields.io/badge/free-open--source-green.svg)
[![Apache JMeter](https://img.shields.io/badge/Apache%20JMeter-green)](https://jmeter.apache.org/)



This program recursively scans a directory looking for files of different types to generate a link or include the content in the generated html page.<br/>
Since version 1.5, a Table Of Content could be computed and add to the result page.

The types (extensions) of files searched are:
 * csv
 * jtl
 * xml
 * gz
 * zip
 * log
 * xlsx
 * xls
 * gif
 * png
 * bmp
 * jpg
 * jpeg
 * html
 * txt
 * htm
 
The link is "&lt;a ref=" relative for files of type: csv, jtl, xml, gz, zip, log, xlsx, xls, txt, htm (to download it)

The link is "<img src=" relative for files of type: gif, png, bmp, jpg, jpeg (to view it)

For html files (.html), the content is read and directly included in the generated html page (blocks of html tables created with the csv-report-to-html tool from csv file). This HTML included file must not have elements &lt;html&gt; and &lt;/html&gt;. **Please note:** ".html" and ".htm" files are treated differently.

Sorting algorithm : Ascending alphabetical sorting but filenames with less directory deep are before file this directory
<pre>
Example : z.img < dir/x.img < dir/y.img < aa/bb/c.img < aa/bb/d.img
Deep    : no dir, dir 1 deep level,      dir 2 deep level
</pre>

## Default values 
- image_width = 1024 (pixels)
- add_toc = true (add Table Of Contents)

And the charset to read could be set with -Dfile.encoding=&lt;charset encoding&gt; (e.g: UFT-8)

Change this values with system properties :
- -Dimage_width=new_int_value (e.g: 1280)
- -Dadd_toc=false (e.g: true or false)

## The index.html generated
HTML Extract example :

![html extract](doc/images/html_extract.png)

## Usage Maven

The maven groupId, artifactId and version, this plugin is in the **Maven Central Repository** [![Maven Central](https://img.shields.io/maven-central/v/io.github.vdaburon/create-html-for-files-in-directory)](https://central.sonatype.com/artifact/io.github.vdaburon/create-html-for-files-in-directory)


```xml
<groupId>io.github.vdaburon</groupId>
<artifactId>create-html-for-files-in-directory</artifactId>
<version>1.8</version>
```
Just include the plugin in your `pom.xml` and execute `mvn verify` <br>
or individual launch `mvn -Dimage_width=950 -Dadd_toc=false exec:java@create_html_page_for_files_in_directory`

```xml
<project>
    <properties>
        <image_width>1024</image_width>
        <add_toc>true</add_toc>
    </properties>

    <dependencies>
        <dependency>
            <groupId>io.github.vdaburon</groupId>
            <artifactId>create-html-for-files-in-directory</artifactId>
            <version>1.8</version>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>exec-maven-plugin</artifactId>
                <version>1.2.1</version>
                <executions>
                    <execution>
                        <id>create_html_page_for_files_in_directory</id>
                        <phase>verify</phase>
                        <goals>
                            <goal>java</goal>
                        </goals>
                        <configuration>
                            <mainClass>io.github.vdaburon.jmeter.utils.HtmlGraphVisualizationGenerator</mainClass>
                            <arguments>
                                <argument>${project.build.directory}/jmeter/results</argument>
                                <argument>index.html</argument>
                            </arguments>
                            <systemProperties>
                                <systemProperty>
                                    <key>image_width</key>
                                    <value>${image_width}</value>
                                </systemProperty>
                                <systemProperty>
                                    <key>add_toc</key>
                                    <value>${add_toc}</value>
                                </systemProperty>
                            </systemProperties>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

## Simple jar tool
This tool is a java jar, so it's could be use as simple jar (look at [Release](https://github.com/vdaburon/CreateHtmlForFilesInDirectory/releases) to download jar file)
<pre>
java -Dimage_width=900 -Dadd_toc=true -cp create-html-for-files-in-directory-&lt;version&gt;.jar io.github.vdaburon.jmeter.utils.HtmlGraphVisualizationGenerator jmeter/results index.html
or
java -Dimage_width=900 -Dadd_toc=true -jar create-html-for-files-in-directory-&lt;version&gt;-jar-with-dependencies.jar jmeter/results index.html
</pre>

Remark : <br/>
The result page could be in the parent directory like : ../index.html

## Link to others projects
Usually this plugin is use with [jmeter-graph-tool-maven-plugin](https://github.com/vdaburon/jmeter-graph-tool-maven-plugin)<br>
and this plugin [csv-report-to-html](https://github.com/vdaburon/JMReportCsvToHtml)
1) The **jmeter-graph-tool-maven-plugin** create the report csv files and graphs
2) The **csv-report-to-html** create the **html table report** from the csv file
3) The **create-html-for-files-in-directory** create a page html this links to images and files in a directory to show and add links

## License
See the LICENSE file Apache 2 [https://www.apache.org/licenses/LICENSE-2.0](https://www.apache.org/licenses/LICENSE-2.0)

## Versions
Version 1.8 date 2025-07-16, Change the maven plugin and configuration to publish to maven central in pom.xml.

Version 1.7 date 2025-07-11, Add extensions ".txt" and ".htm", ".htm" file is not include like ".html" file but like log file with a link a href. It's a solution to open another html page with a link.

Version 1.6 date 2025-04-29, Compute relative path for result page to a parent directory, e.g: directory with file : "c:/dir/image" and result to parent directory "../index.html" links in index.html to relative sub directory "image/"

Version 1.5 date 2025-04-27, Change links name for Table Of Contents to avoid conflict with links in included html page.

Version 1.4 date 2025-04-26, add Table Of Contents and new property add_toc (default value : true).

Version 1.3 date 2023-05-10, Add extensions xlsx and xls (Excel file)

Version 1.2 add the file size after the "&lt;a ref=" link

Version 1.1 add link (a href) to jtl and xml files

Version 1.0 initial version

