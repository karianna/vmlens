/* NOTE this file is autogenerated by Scalate : see http://scalate.fusesource.org/ */
package templates

import _root_.scala.collection.JavaConversions._
import _root_.org.fusesource.scalate.support.TemplateConversions._
import _root_.org.fusesource.scalate.util.Measurements._

object $_scalate_$issuesAsYaml_mustache {
  def $_scalate_$render($_scalate_$_context: _root_.org.fusesource.scalate.RenderContext): Unit = {
    ;{
      val context: _root_.org.fusesource.scalate.RenderContext = $_scalate_$_context.attribute("context")
      import context._
      
      
      import _root_.org.fusesource.scalate.mustache._
      
      val $_scope_1 = Scope($_scalate_$_context)
      $_scope_1.section("issues") { $_scope_2 =>
        $_scalate_$_context << ( "- " )
        $_scope_2.renderVariable("name", true)
        $_scalate_$_context << ( "\n" )
        $_scope_2.section("children4Yaml") { $_scope_3 =>
          $_scope_3.renderVariable("title4Yaml", true)
          $_scalate_$_context << ( ":\n       " )
          $_scope_3.renderVariable("name4Yaml", true)
          $_scalate_$_context << ( "\n       stack:\n       " )
          $_scope_3.section("children4Yaml") { $_scope_4 =>
            $_scope_4.renderVariable("name4Yaml", true)
            $_scalate_$_context << ( "\n       " )
          }
        }
      }
    }
  }
}


class $_scalate_$issuesAsYaml_mustache extends _root_.org.fusesource.scalate.Template {
  def render(context: _root_.org.fusesource.scalate.RenderContext): Unit = $_scalate_$issuesAsYaml_mustache.$_scalate_$render(context)
}