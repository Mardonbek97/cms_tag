<%@ page import="uz.fido_biznes.cms.Sentence" %>
<%@ page import="java.util.Vector" %><%!
	private static Sentence[] sentences;
	private static Vector vSent;
	private static int SI(Sentence sentence) {
		if (vSent == null) vSent = new Vector(100);
		vSent.addElement(sentence);
		return vSent.size() - 1;
	}
  private static int SI(String st1) {
    return SI(st1, st1, st1, st1);
  }
  private static int SI(String st1, String st2) {
    return SI(st1, st2, st2, st1);
  }
  private static int SI(String st1, String st2, String st3) {
    return SI(st1, st2, st3, st1);
  }
  private static int SI(String st1, String st2, String st3, String st4) {
    String _st2 = st2;
    String _st3 = st3;
    String _st4 = st4;
    if (_st2 == null || _st2.trim().length() == 0) {
      if (_st3 == null || _st3.trim().length() == 0) {
        _st2 = st1;
        _st3 = st1;
      } else {
        _st2 = st3;
      }
    }
    if (_st4 == null || _st4.trim().length() == 0) {
      _st4 = st1;
    }
    return SI(new Sentence(st1, _st2, _st3, _st4));
  }
	static 
	{
		if (vSent != null) {
      sentences = new Sentence[vSent.size()];
      for(int i = 0; i < vSent.size(); i++)
        sentences[i] = (Sentence)vSent.elementAt(i);
      vSent = null;
    }
	}
%>