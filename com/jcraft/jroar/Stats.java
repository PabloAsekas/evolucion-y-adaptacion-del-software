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

class Stats extends Page{
  static void register(){
    register("/stats.xml", Stats.class.getName());
  }

  static final private char[] LT="<".toCharArray();
  static final private char[] GT=">".toCharArray();
  static final private char[] LTSLASH="</".toCharArray();

  static final private char[] CLIENTCONNECTIONS="client_connections".toCharArray();
  static final private char[] LIMIT="limit".toCharArray();
  static final private char[] CONNECTIONS="connections".toCharArray();
  static final private char[] SOURCECONNECTIONS="source_connections".toCharArray();
  static final private char[] SOURCES="sources".toCharArray();
  static final private char[] LISTENERS="listeners".toCharArray();

//  public void kick(MySocket s, Hashtable vars, Vector httpheader) throws IOException{
  @Override
  public void kick(MySocket s, HashMap vars, ArrayList httpheader) throws IOException{      
/*
    StringBuffer sb=new StringBuffer();
    sb.append("<?xml version=\"1.0\"?>\n");
    sb.append("<icestats>\n");

    if(HttpServer.client_connections>0){
      indent(sb, 1);
      wrapln(sb, _client_connections, HttpServer.client_connections);
    }

    indent(sb, 1); 
    wrapln(sb, _connections, HttpServer.connections);

    synchronized(Source.sources){
      if(HttpServer.source_connections>0){
      indent(sb, 1); 
      wrapln(sb, _source_connections, HttpServer.source_connections);

      Enumeration keys=Source.sources.keys();
      indent(sb, 1); 
      wrapln(sb, _sources, Source.sources.size());

      if(keys.hasMoreElements()){ 
        for(; keys.hasMoreElements();){
          String mount=((String)(keys.nextElement()));
          Source source=(Source)(Source.getSource(mount));
          indent(sb, 1); sb.append("<source mount=\""+mount+"\">"); ln(sb);

          if(source.getLimit()!=0){
            indent(sb, 2);
            wrapln(sb, _limit, source.getLimit());
	  }

          indent(sb, 2);
          wrapln(sb, _connections, source.getConnections());

          indent(sb, 2);
          wrapln(sb, _listeners, source.getListeners());

          indent(sb, 1); sb.append("</source>"); ln(sb);
	}
      }
      }
    }
    sb.append("</icestats>\n");
    sb.append("\n");
*/

    StringBuilder sb=new StringBuilder();
    sb.append("<?xml version=\"1.0\"?>");
    sb.append("<icestats>");
    if(HttpServer.client_connections>0){
      wrap(sb, CLIENTCONNECTIONS, HttpServer.client_connections);
    }
    wrap(sb, CONNECTIONS, HttpServer.connections);

    synchronized(Source.sources){
      if(HttpServer.source_connections>0){
      wrap(sb, SOURCECONNECTIONS, HttpServer.source_connections);
      Set keys=Source.sources.keySet();
      wrap(sb, SOURCES, Source.sources.size());
      Iterator it = keys.iterator();
      if (it.hasNext()){
          for(;it.hasNext();){
          String mount=((String)(it.next()));
          Source source=(Source)(Source.getSource(mount));
          String x = "<source mount=\""+mount+"\">";
          sb.append(x);

          if(source.getLimit()!=0){
            wrap(sb, LIMIT, source.getLimit());
	  }
          wrap(sb, CONNECTIONS, source.getConnections());
          wrap(sb, LISTENERS, source.getListeners());
          sb.append("</source>");
	}
      }
      /*if(keys.hasMoreElements()){ 
        for(; keys.hasMoreElements();){
          String mount=((String)(keys.nextElement()));
          Source source=(Source)(Source.getSource(mount));
          String x = "<source mount=\""+mount+"\">";
          sb.append(x);

          if(source.getLimit()!=0){
            wrap(sb, LIMIT, source.getLimit());
	  }
          wrap(sb, CONNECTIONS, source.getConnections());
          wrap(sb, LISTENERS, source.getListeners());
          sb.append("</source>");
	}
      }*/
      }
    }
    sb.append("</icestats>");

    String foo=sb.toString();

    s.println("HTTP/1.0 200 OK" );
    s.println("Content-Length: "+foo.length());
    s.println("Content-Type: text/html");
    s.println("") ;
    s.print(foo);
    s.flush();
    s.close();
  }

  static final private char[] blank="  ".toCharArray();
  private void  indent(StringBuilder sb, int foo){
    for(int i=0; i<foo; i++){
      sb.append(blank);
    }
  }

  private void  wrap(StringBuilder sb, char[] tag, int foo){
    //sb.append("<"+tag+">"+foo+"</"+tag+">");
    sb.append(LT); sb.append(tag); sb.append(GT);
    sb.append(foo);
    sb.append(LTSLASH); sb.append(tag); sb.append(GT);
  }
  private void  wrap(StringBuilder sb, char[] tag, String foo){
    //sb.append("<"+tag+">"+foo+"</"+tag+">");
    sb.append(LT); sb.append(tag); sb.append(GT);
    sb.append(foo);
    sb.append(LTSLASH); sb.append(tag); sb.append(GT);
  }
  private void  wrapln(StringBuilder sb, String tag, int foo){
    wrapln(sb, tag.toCharArray(), foo);
  }
  private void  wrapln(StringBuilder sb, String tag, String foo){
    wrapln(sb, tag.toCharArray(), foo);
  }
  private void  wrapln(StringBuilder sb, char[] tag, int foo){
    wrap(sb, tag, foo); ln(sb);
  }
  private void  wrapln(StringBuilder sb, char[] tag, String foo){
    wrap(sb, tag, foo); ln(sb);
  }
  static final char[] LN="\n".toCharArray();
  private void  ln(StringBuilder sb){
    sb.append(LN);
  }
}
