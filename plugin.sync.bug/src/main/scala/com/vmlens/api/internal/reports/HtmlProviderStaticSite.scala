package com.vmlens.api.internal.reports

import java.io._;
import com.vmlens.api._;
import org.fusesource.scalate._
import java.io.PrintStream
import scala.collection.mutable.HashMap;
import scala.collection.mutable.ArrayBuffer;
import org.apache.commons.io.IOUtils
import org.fusesource.scalate.support.StringTemplateSource
import scala.collection.JavaConverters._
import com.vmlens.api.internal._;
import com.anarsoft.race.detection.model.result._;
import java.io._;
import com.anarsoft.integration._;
import com.vmlens.api.internal.gen.Version;


class HtmlProviderStaticSite(val templateName: String) extends HtmlProvider {
 
  
  def write2Stream(path : String , printStream : PrintWriter)
  {
      val map = new HashMap[String,AnyRef];

       map.put("showHeader", new java.lang.Boolean(false))
    
       HtmlProviderStaticSite.addStaticValues(map);
       
       
       
       val engine = new TemplateEngine
       {
       allowReload = false ; 
       }
       
       
     
       
         printStream.println(   engine.layout( templateName    , map.toMap));
  }
  
  
}

object HtmlProviderStaticSite
{
  def addStaticValues(map : HashMap[String,AnyRef])
  {
        // map.put("apacheRoot", "http://localhost")
       
       map.put("apacheRoot", "https://vmlens.com")
       map.put("vmlensVersion", Version.LABEL)
  }
  
  
  
}