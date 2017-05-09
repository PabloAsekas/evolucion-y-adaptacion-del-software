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

class HomePage extends Page{

  static void register(){
    register("/", HomePage.class.getName());
    register("/index.html", HomePage.class.getName());
  }
  private static final int REFRESH=60;
  private static int count=0;

  @Override
  public void kick(MySocket s, HashMap vars, ArrayList httpheader) throws IOException{
    count++;
    s.pn( "HTTP/1.0 200 OK" );
    s.pn( "Content-Type: text/html" );
    s.pn( "" ) ;
    s.pn("<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">");
    s.pn("<HTML><HEAD>");
//  s.pn("<META HTTP-EQUIV=\"refresh\" content=\""+REFRESH+";URL=/\">");
    s.p("<TITLE>JRoar "); s.p(JRoar.version); s.p(" at "); 
                          s.p(HttpServer.myURL); s.pn("/</TITLE>");
    s.pn("<meta http-equiv='Content-Type' content='text/html; charset=utf-8' />");
    s.pn("<link rel='stylesheet' href='https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css'>");
    s.pn("<script src='https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js'></script>");
    s.pn("<script src='https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js'></script>");
    s.pn("<link href='http://www.conocemadrid.esy.es/jroar.css' rel='stylesheet' type='text/css' media='all' />");                      
    s.pn("</HEAD><BODY>");
    //s.p( "<h1>JRoar "); s.p(JRoar.version); s.p(" at ");
    //                    s.p(HttpServer.myURL); s.pn("/</h1>");
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
"        <li class='active'><a href='#'>Panel de control</a></li>\n" +
"        <li><a href='#'>Page 2</a></li>\n" +
"        <li><a href='#'>Page 3</a></li>\n" +
"      </ul>\n" +
"    </div>\n" +
"  </div>\n" +
"</nav>");
    Set keys=Source.sources.keySet();
    Iterator it = keys.iterator();
    if(it.hasNext()){ 
      //s.pn("Mount points.<br>"); 
    }
    else{ s.pn("<div class='container'>\n" +
"  <div class='alert alert-warning'>\n" +
"    <strong>¡Atención!</strong> No existe ningún punto de montaje <a href='/ctrl.html' class='alert-link'>Pulse aquí para agregar uno</a>.\n" +
"  </div>\n" +
"</div>"); }

    s.pn("<table cellpadding=3 cellspacing=0 border=0>");
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
        s.pn("<td align=left>");
        s.p("<a href="); s.p(source_name); s.p(">");
        s.p(source_name); s.p("</a>");
        s.pn("</td>");
      }
      else if(source instanceof UDPSource){
        UDPSource foo=(UDPSource)source;
        s.pn("<td align=left>");
        s.p(foo.b.srcmpoint);
        s.pn("</td>");
      }
      else{
        //s.p("<td align=left>"); s.p(source_name); s.pn("</td>"); //playlist
      }
      s.pn("</tr>");

      /*
      dumpComment(s, source.current_comment);
      */

      /*
      String comment=getComment(source.current_comment);
      if(comment!=null){
        s.pn("<tr>");
        s.pn("<td>&nbsp;</td>");
        s.pn("<td>&nbsp;</td>");
        s.p("<td>"); s.p(comment); s.pn("</td>");
        s.pn("</tr>");
      }
      */

      Object[] proxies=source.getProxies();
      if(proxies!=null){
          for (Object proxie : proxies) {
              String foo = (String) (proxie);
              s.pn("<tr>");
              s.pn("<td>&nbsp;</td>");
              s.pn("<td nowrap>---&gt</td>");
              String host=getHost(foo);
              if(host==null){
                  s.p("<td><a href="); s.p(ogg2m3u(foo)); s.p(">"); s.p(foo);
                  s.pn("</a></td>");
              }
              else{
                  s.p("<td><a href="); s.p(ogg2m3u(foo)); s.p(">"); s.p(foo.substring(host.length()-1)); s.p("</a>&nbsp;at&nbsp;");
                  s.p("<a href="); s.p(host); s.p(">"); s.p(host); s.pn("</a></td>");
              }   s.pn("</tr>");
          }
      }

    }
    s.pn("</table>");

    s.pn("<hr width=80%>");

    s.pn("<table width=100%>");
    s.pn("<tr><td align=\"right\"><a style='margin-right:25px;' type=\"button\" href=\"/ctrl.html\" class=\"btn btn-primary btn-lg\"><span class=\"glyphicon glyphicon-cog\"></span>&nbsp;Control</a></td></tr>");
    s.p("<tr><td align=\"right\"><small><i>"); s.p(count); s.pn("</i></small></td></tr>");
    s.pn("</table>");

    s.pn("</BODY></HTML>");
    s.flush();
    s.close();

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
  private void dumpComment(MySocket s, Comment c) throws IOException{
    if(c.comments==0)return;
    s.pn("<tr>");
    s.pn("<td>&nbsp;</td>"); s.pn("<td>&nbsp;</td>");
    s.p("<td>");
    for(int i=0; i<c.comments; i++){
      s.p(new String(c.user_comments[i], 0, c.user_comments[i].length-1));
      if(i+1<c.comments) s.p("<br>");
    }
    s.pn("</td>");
    s.pn("</tr>");
  }
*/
}
