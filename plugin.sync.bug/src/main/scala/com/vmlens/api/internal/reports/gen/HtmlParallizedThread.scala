/* NOTE this file is autogenerated by Scalate : see http://scalate.fusesource.org/ */
package templates

import _root_.scala.collection.JavaConversions._
import _root_.org.fusesource.scalate.support.TemplateConversions._
import _root_.org.fusesource.scalate.util.Measurements._

object $_scalate_$htmlParallizedThread_mustache {
  def $_scalate_$render($_scalate_$_context: _root_.org.fusesource.scalate.RenderContext): Unit = {
    ;{
      val context: _root_.org.fusesource.scalate.RenderContext = $_scalate_$_context.attribute("context")
      import context._
      
      
      import _root_.org.fusesource.scalate.mustache._
      
      val $_scope_1 = Scope($_scalate_$_context)
      $_scope_1.partial("header")
      $_scalate_$_context << ( "\n\n\n<div class=\"" )
      $_scope_1.renderVariable("containerTyp", false)
      $_scalate_$_context << ( "\">\n \n" )
      $_scope_1.section("issues") { $_scope_2 =>
        $_scalate_$_context << ( "<div class=\"row\">\n    <div>\n      <h2 class=\"text-left\">Accessing Threads:</h1>\n    </div>\n</div>\n \n \n<div class=\"row text-left\">\n  \n  \n <table class=\"table table-striped\" >\n  <tr>\n    <th>Thread</th>\n    <th>Shared State</th>\n    <th>Contains Monitors?</th>\n  </tr>\n  \n\n\n" )
        $_scope_2.section("threads") { $_scope_3 =>
          $_scalate_$_context << ( "<tr>\n    <td><a   href=\"" )
          $_scope_3.renderVariable("link", false)
          $_scalate_$_context << ( "\" >" )
          $_scope_3.renderVariable("name", false)
          $_scalate_$_context << ( "</a></td>\n    <td>" )
          $_scope_3.renderVariable("stateText", false)
          $_scalate_$_context << ( "</td>\n    <td>" )
          $_scope_3.renderVariable("monitorText", false)
          $_scalate_$_context << ( "</td>\n  </tr>\n\n" )
        }
        $_scalate_$_context << ( "</table>   \n    \n     \n</div>\n  \n \n \n \n  <div class=\"row\">\n    <div>\n      <h2 class=\"text-left\">Shared State:</h1>\n    </div>\n</div>\n \n \n \n<div class=\"row text-left\">\n  \n  \n <table class=\"table table-striped\" >\n  <tr>\n    <th>Memory Access</th>\n    <th>Waitpoint Type</th>\n    <th>Waitpoint State</th>\n  </tr>\n  \n\n\n" )
        $_scope_2.section("state") { $_scope_4 =>
          $_scalate_$_context << ( "<tr>\n<td>" )
          $_scope_4.renderVariable("name", false)
          $_scalate_$_context << ( "\n\n" )
          $_scope_4.section("hasMemoryUnderMonitor") { $_scope_5 =>
            $_scalate_$_context << ( "<ul>\n\n" )
            $_scope_5.section("memoryUnderMonitor") { $_scope_6 =>
              $_scalate_$_context << ( "<li>" )
              $_scope_6.renderVariable("name", false)
              $_scalate_$_context << ( "</li>\n" )
            }
            $_scalate_$_context << ( "</ul>\n\n" )
          }
          $_scalate_$_context << ( "</td> \n" )
          $_scope_4.section("reaonWhyNotSet") { $_scope_7 =>
            $_scalate_$_context << ( "<td colspan=\"2\" class=\"text-center\">None, " )
            $_scope_7.renderVariable("name", false)
            $_scalate_$_context << ( "</td>\n\n" )
          }
          $_scope_4.section("reportWaitpointTypeAndState") { $_scope_8 =>
            $_scalate_$_context << ( "<td>" )
            $_scope_8.renderVariable("waitpointType", false)
            $_scalate_$_context << ( "</td>\n<td>" )
            $_scope_8.renderVariable("state", false)
            $_scalate_$_context << ( "</td>\n" )
          }
          $_scalate_$_context << ( "</tr>\n\n" )
        }
        $_scalate_$_context << ( "</table>   \n    \n     \n</div> \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n " )
      }
      $_scalate_$_context << ( "</div>\n\n\n\n\n" )
      $_scope_1.partial("footer")
    }
  }
}


class $_scalate_$htmlParallizedThread_mustache extends _root_.org.fusesource.scalate.Template {
  def render(context: _root_.org.fusesource.scalate.RenderContext): Unit = $_scalate_$htmlParallizedThread_mustache.$_scalate_$render(context)
}
