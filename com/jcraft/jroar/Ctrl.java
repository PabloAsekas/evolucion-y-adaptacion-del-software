/* -*-mode:java; c-basic-offset:2; -*- */
/* JRoar -- pure Java streaming server for Ogg 
 *
 * Copyright (C) 2001,2002 ymnk, JCraft,Inc.
 *
 * Written by: 2001,2002 ymnk<ymnk@jcraft.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package com.jcraft.jroar;
import java.io.*;
import java.util.*;

class Ctrl extends Page{

  static void register(){
    register("/ctrl.html", Ctrl.class.getName());
  }
  private static final int REFRESH=60;
  private static int count=0;

  @Override
    public void kick(MySocket s, HashMap vars, ArrayList httpheader) throws IOException{
    count++;
    s.println( "HTTP/1.0 200 OK" );
    s.println( "Content-Type: text/html" );
    s.println( "" ) ;
    s.println("<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">");
    s.println("<HTML><HEAD>");
//  s.println("<META HTTP-EQUIV=\"refresh\" content=\""+REFRESH+";URL=/ctrl.html\">");
    s.println("<TITLE>JRoar "+JRoar.version+" at "+HttpServer.myURL+"/</TITLE>");
    s.pn("<meta http-equiv='Content-Type' content='text/html; charset=utf-8' />");
    s.pn("<link rel='stylesheet' href='https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css'>");
    s.pn("<script src='https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js'></script>");
    s.pn("<script src='https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js'></script>");
    s.println("<link href='http://www.conocemadrid.esy.es/jroar.css' rel='stylesheet' type='text/css' media='all' />");
    s.println("</HEAD><BODY>");
    Set keys=Source.sources.keySet();
    Iterator it = keys.iterator();
    if(it.hasNext()){ 
      //s.println("Mount points.<br>"); 
    }
    else{ //s.println("There is no mount point.<br>");   
    }
    //añado a partir de aqui lo nuevo
    s.pn("<nav class='navbar navbar-inverse'>\n" +
"  <div class='container-fluid'>\n" +
"    <div class='navbar-header'>\n" +
"      <button type='button' class='navbar-toggle' data-toggle='collapse' data-target='#myNavbar'>\n" +
"        <span class='icon-bar'></span>\n" +
"        <span class='icon-bar'></span>\n" +
"        <span class='icon-bar'></span>                        \n" +
"      </button>\n" +
"      <a class='navbar-brand' href='#'>JRoar v."); s.pn(JRoar.version); s.pn("</a>\n" +
"    </div>\n" +
"    <div class='collapse navbar-collapse' id='myNavbar'>\n" +
"      <ul class='nav navbar-nav'>\n" +
"        <li><a href='/'>Home</a></li>\n" +
"        <li class='active'><a href='/ctrl.html'>Panel de control</a></li>\n" +
"      </ul>\n" +
"    </div>\n" +
"  </div>\n" +
"</nav>");
    s.println("<table cellpadding=3 cellspacing=0 border=0>");
    for(; it.hasNext();){
      String mountpoint=((String)(it.next()));
      Source source=Source.getSource(mountpoint); if(source==null) continue;
      String source_name=source.source;

      
      s.pn("<div class='container'><div class='alert alert-success'>\n" +
"  Estamos sonando en <strong>");
      s.p("<a href="); s.p(ogg2m3u(mountpoint)); s.p(">"); s.p(mountpoint);
      if(source instanceof UDPSource){
         UDPSource foo=(UDPSource)source;
         s.p("(UDP:"); s.p(foo.b.port); s.p(")");
      }
      s.p("</a>");
      s.p("&nbsp;"); 
      s.p("Con un total de "); s.p(source.getListeners()); s.p("&nbsp;<span class=\"glyphicon glyphicon-headphones\"></span> oyentes, y ");
                               s.p(source.getConnections()); s.p("&nbsp;<span class=\"glyphicon glyphicon-user\"></span>&nbsp;conexiones");

      s.pn("</div></div>");


      if(source instanceof Proxy){
        s.println("<td align=left>");
        s.print("<a href="+source_name+">"+source_name+"</a>");
        s.println("</td>");
      }
      else if(source instanceof UDPSource){
        UDPSource foo=(UDPSource)source;
        s.println("<td align=left>");
        s.print(foo.b.srcmpoint);
        s.println("</td>");
      }
      else{
        //s.println("<td align=left>"+source_name+"</td>"); playlist
      }
      s.println("</tr>");
      /*
      String comment=getComment(source.current_comment);
      if(comment!=null){
        s.println("<tr>");
        s.println("<td>&nbsp;</td>");
        s.println("<td>&nbsp;</td>");
        s.println("<td>"+comment+"</td>");
        s.println("</tr>");
      }
      */
      Object[] proxies=source.getProxies();
      if(proxies!=null){
          for (Object proxie : proxies) {
              String foo = (String) (proxie);
              s.println("<tr>");
              s.println("<td>&nbsp;</td>");
              s.println("<td nowrap>---&gt</td>");
              String host=getHost(foo);
              if(host==null){
                  s.println("<td><a href="+ogg2m3u(foo)+">"+foo+"</a></td>");
              }
              else{
                  s.println("<td><a href="+ogg2m3u(foo)+">"+foo.substring(host.length()-1)+"</a>&nbsp;at&nbsp;<a href="+host+">"+host+"</a></td>");
              }   s.println("</tr>");
          }
      }

    }
    s.println("</table>");

    //si no hay puntos de montje:
    s.println("<div class='container-fluid' style='padding:35px;'>\n" +
"  <div class='row'>\n" +
"      <div class='col-md-6 col-md-offset-3' style='background-color:#eceff1; padding:35px; border-radius:15px'>\n" +
"        <div class='row'>\n" +
"                  <h3 align='center'>Montar Streaming</h3>\n" +
"            <form method=post action=/mount>\n" +
"            <div class='form-group'>\n" +
"                  <label>Mount:</label>\n" +
"                  <input class='form-control' type=text name=mountpoint value='/'/>\n" +
"            </div>\n" +
"            <div class='form-group'>\n" +
"                  <label>Source:</label>\n" +
"                  <input class='form-control' type=text name=source value='http://'>\n" +
"            </div>\n" +
"            <div class='form-group'>\n" +
"                  <select name='livestream'>Source:\n" +
"                    <option value='true' selected>Livestream</option>\n" +
"                    <option value='false'>Playlist</option>\n" +
"                  </select>\n" +
"             </div>\n" +
"            <div class='form-group'>\n" +
"                  <label>Limit:</label>\n" +
"                  <input class='form-control' type=text name=limit value=''>\n" +
"                  \n" +
"            </div>\n" +
"            <div class='form-group'>\n" +
"                  <label>Password:</label>\n" +
"                  <input class='form-control' type=password name=passwd value=''>\n" +
"            </div>\n" +
"            <button type='submit' class='btn btn-primary btn-block' name='Mount' value='Mount' >Montar</button>\n" +
"            </form>\n" +
"        </div>\n" +
"      </div>\n" +
"  </div>\n" +
"</div>");
   
    //
    s.print("<p>");

    synchronized(Client.clients){

    keys=Source.sources.keySet();
    it = keys.iterator();
    if(it.hasNext()){
    s.println("<hr width=80%>");
    
    //aqui empiezan las opciones de control cuando se ha montado un streaming
    //acordeon 1
    s.println("<h3 align='center'>Opciones de los streamings</h3>");
    s.println("<div class=\"container\">\n" +
"  <div class=\"panel-group\" id=\"accordion\">\n" +
"    <div class=\"panel panel-default\">\n" +
"      <div class=\"panel-heading\">\n" +
"        <h4 class=\"panel-title\">\n" +
"          <a data-toggle=\"collapse\" data-parent=\"#accordion\" href=\"#collapse1\">Drop</a>\n" +
"        </h4>\n" +
"      </div>\n" +
"      <div id=\"collapse1\" class=\"panel-collapse collapse\">\n");
    //inicio del contenido del acordeon
    s.println("<div class='container-fluid' style='padding:35px;'>\n" +
"  <div class='row'>\n" +
"      <div class='col-md-6 col-md-offset-3' style='background-color:#eceff1; padding:35px; border-radius:15px'>\n" +
"        <div class='row'>\n" +
"                  <h3 align='center'>Drop</h3>\n" +
"            <form method='post' action=/drop>\n" +
"            <div class='form-group'>\n" +
"                  <label>Drop:</label>\n" +
"                  <select name=mpoint size=1>\n");
    for(; it.hasNext();){
      String mpoint=((String)(it.next()));
      s.println("<OPTION VALUE="+mpoint+">"+mpoint);
    }
    s.print("</select>\n" +
"            </div>\n" +  
"            <div class='form-group'>\n" +
"                  <label>Drop:</label>\n" +
"                  <input class='form-control' value='/'/>\n" +
"            </div>\n" + 
"            <div class='form-group'>\n" +
"                  <label>Password:</label>\n" +
"                  <input class='form-control' type=password name=passwd value='' length=8>\n" +
"            </div>\n" +
"            <input type=submit name=Drop value=Drop>\n" +            
"            </form>\n" +
"        </div>\n" +
"      </div>\n" +
"  </div>\n" +
"</div>");
    //fin del contenido del acordeon 1
    s.println("</div>\n" +
"           </div>");

    }
//fin acordeon 1
    keys=Source.sources.keySet();
    it = keys.iterator();
    if(it.hasNext()){
    //acordeon 2
    s.println("<div class=\"panel panel-default\">\n" +
"      <div class=\"panel-heading\">\n" +
"        <h4 class=\"panel-title\">\n" +
"          <a data-toggle=\"collapse\" data-parent=\"#accordion\" href=\"#collapse2\">Shout</a>\n" +
"        </h4>\n" +
"      </div>\n" +
"      <div id=\"collapse2\" class=\"panel-collapse collapse\">\n");
    //inicio contenido acordeon 2
        s.println("<div class='container-fluid' style='padding:35px;'>\n" +
"  <div class='row'>\n" +
"      <div class='col-md-6 col-md-offset-3' style='background-color:#eceff1; padding:35px; border-radius:15px'>\n" +
"        <div class='row'>\n" +
"                  <h3 align='center'>Shout</h3>\n" +
"            <form method='post' action=/shout>\n" +
"            <div class='form-group'>\n" +
"                  <label>Shout:</label>\n" +
"                  <select name=srcmpoint size=1>\n");

    //ESTE APARTADO NO ESTA PASADO A INTERFAZ
    s.println("<table cellpadding=3 cellspacing=0 border=0>");
    for(int i=0; i<Client.clients.size(); i++){
      //Client c=((Client)(Client.clients.elementAt(i)));
      Client c=((Client)(Client.clients.get(i)));
      if(c instanceof ShoutClient){
      ShoutClient sc=(ShoutClient)c;
      s.println("<tr>");

      s.println("<td align=left>");
      s.print(sc.srcmpoint);
      s.println("</td>");

      s.println("<td nowrap>---&gt</td>");

      s.println("<td align=left>");
      s.print("<a href=http://"+sc.dsthost+":"+sc.dstport+sc.dstmpoint+">http://"+sc.dsthost+":"+sc.dstport+sc.dstmpoint+"</a>");
      s.println("</td>");

      s.println("</tr>");
      }
    }  
    s.println("</table>");
    //
    
    for(; it.hasNext();){
      String mpoint=((String)(it.next()));
      if(Source.sources.get(mpoint) instanceof UDPSource) continue;
      s.println("<OPTION VALUE="+mpoint+">"+mpoint);
    }
    s.print("</select>\n" +
"            </div>\n" +  
"            <div class='form-group'>\n" +
"                  <label>Shout:</label>\n" +
"                  <input class='form-control' value='ice://'size=20 maxlength=50/>\n" +
"            </div>\n" + 
"            <div class='form-group'>\n" +
"                  <label>Ice-Password:</label>\n" +
"                  <input class='form-control' type=password name=ice-passwd value='' size='8' maxlength=8>\n" +
"            </div>\n" +
"            <div class='form-group'>\n" +
"                  <label>Password:</label>\n" +
"                  <input class='form-control' type=password name=passwd value=''length=8>\n" +
"            </div>\n" +
"            <input type=submit name=Shout value=Shout>\n" +            
"            </form>\n" +
"        </div>\n" +
"      </div>\n" +
"  </div>\n" +
"</div>");
    //fin del contenido del acordeon 2
    s.println("</div>\n" +
"           </div>");

    }
//fin acordeon 2

    keys=Source.sources.keySet();
    it = keys.iterator();
    if(it.hasNext()){
             //acordeon 3
    s.println("<div class=\"panel panel-default\">\n" +
"      <div class=\"panel-heading\">\n" +
"        <h4 class=\"panel-title\">\n" +
"          <a data-toggle=\"collapse\" data-parent=\"#accordion\" href=\"#collapse3\">UDP Broadcast</a>\n" +
"        </h4>\n" +
"      </div>\n" +
"      <div id=\"collapse3\" class=\"panel-collapse collapse\">\n");
    //inicio contenido acordeon 3
    s.println("<div class='container-fluid' style='padding:35px;'>\n" +
"  <div class='row'>\n" +
"      <div class='col-md-6 col-md-offset-3' style='background-color:#eceff1; padding:35px; border-radius:15px'>\n" +
"        <div class='row'>\n" +
"                  <h3 align='center'>UDP Broadcast</h3>\n" +
"            <form method='post' action=/udp>\n" +
"            <div class='form-group'>\n" +
"                  <label>UDP Broadcast:</label>\n" +
"                  <select name=srcmpoint size=1>\n");
    for(; it.hasNext();){
      String mpoint=((String)(it.next()));
      Source source=Source.getSource(mpoint); 
      if(source==null || source instanceof UDPSource) continue;
      s.println("<OPTION VALUE="+mpoint+">"+mpoint);
    }
    s.print("</select>\n" +
"            </div>\n" +  
"            <div class='form-group'>\n" +
"                  <label>Port:</label>\n" +
"                  <input class='form-control' name=port value='' size=4 maxlength=4/>\n" +
"            </div>\n" + 
"            <div class='form-group'>\n" +
"                  <label>Broadcast address:</label>\n" +
"                  <input class='form-control' type=text name=baddress value='' size=15 maxlength=20>\n" +
"            </div>\n" +
"            <div class='form-group'>\n" +
"                  <label>Mountpoint:</label>\n" +
"                  <input class='form-control' name=dstmpoint value='/' size=20 maxlength=50>\n" +
"            </div>\n" +
"            <div class='form-group'>\n" +
"                  <label>Password:</label>\n" +
"                  <input class='form-control' type=password name=passwd value='' length=8>\n" +
"            </div>\n" +
"            <input type=submit name=Broadcast value=Broadcast>\n" +            
"            </form>\n" +
"        </div>\n" +
"      </div>\n" +
"  </div>\n" +
"</div>");
        //fin del contenido del acordeon 3
    s.println("</div>\n" +
"           </div>");
    }
    //fin acordeon 3
    }

    s.println("<hr width=80%>");

    s.println("<table width=100%><tr>");
    s.p("<tr><td align=\"right\"><br><div class=\"alert alert-warning\" style=\"width: 150px; margin-right:20px;\">\n" +
"  <small>Número de Streamings montados: &nbsp<i>"); s.p(count); s.pn("</i></small></div></td></tr>");
    s.println("</tr></table>");

    s.println("</BODY></HTML>");
    s.flush();
    s.close();

//  System.gc();
  }
  /*
  private String ogg2pls(String ogg){
    if(!ogg.endsWith(".ogg") && !ogg.endsWith(".spx")) return ogg;
    byte[] foo=ogg.getBytes();
    foo[foo.length-1]='s';foo[foo.length-2]='l';foo[foo.length-3]='p';
    return new String(foo);
  }
  */
  private String ogg2m3u(String ogg){
    if(!ogg.endsWith(".ogg") && !ogg.endsWith(".spx")) return ogg;
    byte[] foo=ogg.getBytes();
    foo[foo.length-1]='u';foo[foo.length-2]='3';foo[foo.length-3]='m';
    return new String(foo);
  }
  private static final String _http="http://";
  private String getHost(String url){
    if(!url.startsWith(_http)) return null;
    int foo=url.substring(_http.length()).indexOf('/');
    if(foo!=-1){
      return url.substring(0, _http.length()+foo+1);
    }
    return null;
  }

  /*
  // hmm...
  private String getComment(Comment c){
    if(c.comments==0)return null;
    StringBuffer sb=new StringBuffer();
    for(int i=0; i<c.comments; i++){
      sb.append(new String(c.user_comments[i], 0, c.user_comments[i].length-1));
      if(i+1<c.comments) sb.append("<br>");
    }
    return sb.toString();
  }
  */
}
