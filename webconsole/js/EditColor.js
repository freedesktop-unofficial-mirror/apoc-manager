//
// DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
// 
// Copyright 2007 Sun Microsystems, Inc. All rights reserved.
// 
// The contents of this file are subject to the terms of either
// the GNU General Public License Version 2 only ("GPL") or
// the Common Development and Distribution License("CDDL")
// (collectively, the "License"). You may not use this file
// except in compliance with the License. You can obtain a copy
// of the License at www.sun.com/CDDL or at COPYRIGHT. See the
// License for the specific language governing permissions and
// limitations under the License. When distributing the software,
// include this License Header Notice in each file and include
// the License file at /legal/license.txt. If applicable, add the
// following below the License Header, with the fields enclosed
// by brackets [] replaced by your own identifying information:
// "Portions Copyrighted [year] [name of copyright owner]"
// 
// Contributor(s):
// 
// If you wish your version of this file to be governed by
// only the CDDL or only the GPL Version 2, indicate your
// decision by adding "[Contributor] elects to include this
// software in this distribution under the [CDDL or GPL
// Version 2] license." If you don't indicate a single choice
// of license, a recipient has the option to distribute your
// version of this file under either the CDDL, the GPL Version
// 2 or to extend the choice of license to its licensees as
// provided above. However, if you add GPL Version 2 code and
// therefore, elected the GPL Version 2 license, then the
// option applies only if the new code is made subject to such
// option by the copyright holder.
//

// Masthead location
        var theMasthead = self.opener.parent.parent.parent.opener;
        var contentForm = self.opener.document.Form;
        var notSet = self.opener.localizedNotSet;
// Old Color and Current Color
        var data = new Array();
        params=window.location.search;
        params=params.substr(1);
        var pairs = params.split("&");
        for (var i=0;i<pairs.length;i++) {
            var nameVal = pairs[i].split("=");
            var name = nameVal[0];
            var value = nameVal[1];
            data[name] = value;
        }

        oldcol = decodeURI(data["OrgColor"]);
        if ((oldcol == null) || (oldcol=="undefined") || (oldcol=="") || (oldcol==notSet)){
                oldcol="ffffff";
        } 
        property = data["Prop"];
        if (data["Prop"] == null){
            property = self.opener.parent.masthead.editColorProp;
        } else {
            if (property=="undefined" || property=="") {
                property="";
            }
            theMasthead.editColorProp=property;
        }
        currentName = decodeURI(data["Name"]);
        if (data["Name"] == null || currentName=="undefined"){
            currentName = theMasthead.currentName;
        } else {
            if ((currentName=="undefined")
                    || (currentName=="") 
                        || (currentName==notSet)) {
                currentName="";              
            }
            theMasthead.currentName=currentName;
        }
        origName=currentName;
        curcol=oldcol;
        // Predefined Colors Cursor
        cursorImg=new Image ();
        cursorImg.src="/apoc/images/cursor.gif";
        blankImg=new Image ();
        blankImg.src="/com_sun_web_ui/images/other/dot.gif";
        xImg=new Image ();
        xImg.src="/apoc/images/x.png";
        // this is so that if a new color is added the cursor will point to the new
        // color on refresh
        var cursorPos = 0;
        if ((theMasthead.cursorPos=="") ||
            (theMasthead.cursorPos==null)) {
            cursorPos=0;
        } else {
            cursorPos=theMasthead.cursorPos;
            theMasthead.cursorPos="";
        }

    // Other Stuff
        hexchars="0123456789abcdef";
        currgb=[fromhex(curcol.substr(0,2)),fromhex(curcol.substr(2,2)),fromhex(curcol.substr(4,2))];
        curhsl=RGBtoHSL(currgb[0],currgb[1],currgb[2]);
    // Funktions	

	function fromhex(inval) {
		out=0;
		for (a=inval.length-1;a>=0;a--)  {
                        var aChar = inval.charAt(a);
                        aChar = aChar.toLowerCase();
			out+=Math.pow(16,inval.length-a-1)*hexchars.indexOf(aChar);	
                }
		return out;
	}

	function tohex(inval) {
		out=hexchars.charAt(inval/16);
		out+=hexchars.charAt(inval%16);
		return out;
	}


	function setCursor(what) {
        var alt = document.getElementById("imgCol"+cursorPos).alt;
        if (alt == "" || alt == "undefined") {
            document.getElementById("imgCol"+cursorPos).src=xImg.src;
        } else {
            document.getElementById("imgCol"+cursorPos).src=blankImg.src;
		}
        cursorPos=what;
		document.getElementById("imgCol"+cursorPos).src=cursorImg.src;
	}

    function setName(pos) {
        var alt = document.getElementById("imgCol"+pos).alt;
        if(alt == "undefined") {
            alt="";
        }
        document.getElementById("EditColor.NameValue").value=alt;
    }

    function updateName() {
        document.getElementById("EditColor.NameValue").value=currentName;
    }

    function setInitialCursorPos() {
        for (i=0; i<96; i++) {
            var eachCol = document.getElementById("defCol"+i).bgColor;
            col = "#"+curcol;
            if (eachCol==col) {
                setCursor(i);
                break;
            }
        }
    }

	function update() {
        var bgColor = "#"+curcol;
		document.getElementById("thecell").bgColor=bgColor;
		document.getElementById("EditColor.RedValue").value=currgb[0];
		document.getElementById("EditColor.GreenValue").value=currgb[1];
		document.getElementById("EditColor.BlueValue").value=currgb[2];
		document.getElementById("EditColor.HtmlValue").value=curcol;
		document.getElementById("EditColor.HueValue").value=curhsl[0];
		document.getElementById("EditColor.SatValue").value=curhsl[1];
		document.getElementById("EditColor.LumValue").value=curhsl[2];
                setCursor(cursorPos);
		
		// set the cross on the colorpic
		var cross=document.getElementById("cross").style;
		var cp=document.getElementById("colorpic");
		xd=0;yd=0;lr=cp;
		while(lr!=null) {xd+=lr.offsetLeft; yd+=lr.offsetTop; lr=lr.offsetParent;}
		cross.top=((yd-9+128)-(127*(curhsl[1]/255)))+"px";
		cross.left=((xd-9)+(127*(curhsl[0]/255)))+"px";
		// update slider pointer
		var sa=document.getElementById("sliderarrow").style;
		var sp=document.getElementById("slider");
		xd=0;yd=0;lr=sp;
		while(lr!=null) {xd+=lr.offsetLeft; yd+=lr.offsetTop; lr=lr.offsetParent;}
		sa.top=yd+"px";
                // minus 4 from the result cause the location is for the left
                // side of the image and not the pointer bit
		sa.left=(xd+Math.round((127*(255-curhsl[2]))/255)+-4)+"px"
		// update slider colors
		for (i=0;i<128;i++) {
			rgb=HSLtoRGB(curhsl[0],curhsl[1],255-255*i/127);
                        var hex = "#"+(tohex(rgb[0])+tohex(rgb[1])+tohex(rgb[2]));
			document.getElementById("sc"+(i+1)).bgColor=hex;
		}
	}

	function HSLtoRGB (h,s,l) {
		if (s == 0) return [l,l,l] // achromatic
		h=h*360/255;s/=255;l/=255;
		if (l <= 0.5) rm2 = l + l * s;
		else rm2 = l + s - l * s;
		rm1 = 2.0 * l - rm2;
		return [ToRGB1(rm1, rm2, h + 120.0),ToRGB1(rm1, rm2, h),ToRGB1(rm1, rm2, h - 120.0)];
	}

	function ToRGB1(rm1,rm2,rh) {
		if      (rh > 360.0) rh -= 360.0;
		else if (rh < 0.0) rh += 360.0;
 		if      (rh < 60.0) rm1 = rm1 + (rm2 - rm1) * rh / 60.0;
		else if (rh < 180.0) rm1 = rm2;
		else if (rh < 240.0) rm1 = rm1 + (rm2 - rm1) * (240.0 - rh) / 60.0; 
 		return Math.round(rm1 * 255);
	}

	function RGBtoHSL (r,g,b) {
		min = Math.min(r,Math.min(g,b));
		max = Math.max(r,Math.max(g,b));
		// l
		l = Math.round((max+min)/2);
		// achromatic ?
		if(max==min) {h=160;s=0;}
		else {
		// s
			if (l<128) s=Math.round(255*(max-min)/(max+min));
			else s=Math.round(255*(max-min)/(510-max-min));
		// h	
			if (r==max)	h=(g-b)/(max-min);
			else if (g==max) h=2+(b-r)/(max-min);
			else h=4+(r-g)/(max-min);
			h*=60;
			if (h<0) h+=360;
			h=Math.round(h*255/360);
		}
		return [h,s,l];
	}

	function setCol(value) {
		value=value.toLowerCase();
		if (value.length!=6) value=curcol;
		for (a=0;a<6;a++)
			if (hexchars.indexOf(value.charAt(a))==-1) {
				value=curcol;break;
			}
		curcol=value;
		currgb=[fromhex(curcol.substr(0,2)),fromhex(curcol.substr(2,2)),fromhex(curcol.substr(4,2))];
		curhsl=RGBtoHSL(currgb[0],currgb[1],currgb[2]);
		update();
	}

	function setRGB(r,g,b) {
		if (r>255||r<0||g>255||g<0||b>255||b<0) {r=currgb[0];g=currgb[1];b=currgb[2];}
		currgb=[r,g,b];
		curcol=tohex(r)+tohex(g)+tohex(b);
		curhsl=RGBtoHSL(r,g,b);
		update();
	}

	function setHSL(h,s,l) {
		if (h>255||h<0||s>255||s<0||l>255||l<0) {h=curhsl[0];s=curhsl[1];l=curhsl[2];}
		curhsl=[h,s,l];
		currgb=HSLtoRGB(h,s,l);
		curcol=tohex(currgb[0])+tohex(currgb[1])+tohex(currgb[2]);
		update();
	}
	
	function setFromRGB () {
		r=document.getElementById("EditColor.RedValue").value;
		g=document.getElementById("EditColor.GreenValue").value;
		b=document.getElementById("EditColor.BlueValue").value;
		if (r>255||r<0||g>255||g<0||b>255||b<0) {setRGB(currgb[0],currgb[1],currgb[2]);return;}
		setRGB(r,g,b);
	}

	function setFromHTML () {
		inval=document.getElementById("EditColor.HtmlValue").value.toLowerCase();
		if (inval.length!=6) {setCol(curcol);return;}
		for (a=0;a<6;a++)
			if (hexchars.indexOf(inval.charAt(a))==-1) {
				setCol(curcol);return;
			}
		setCol(inval);
	}

	function setFromHSL () {
		h=document.getElementById("EditColor.HueValue").value;
		s=document.getElementById("EditColor.SatValue").value;
		l=document.getElementById("EditColor.LumValue").value;
		if (h>255||h<0||s>255||s<0||l>255||l<0) {setHSL(curhsl[0],curhsl[1],curhsl[2]);return;}
		setHSL(h,s,l);
	}

	function setFromImage (event) {
                document.getElementById("EditColor.NameValue").value="";
		var x=event.offsetX;
		var y=event.offsetY;
		if (x == undefined) {
			xd=0;yd=0;lr=document.getElementById("colorpic");
			while(lr!=null) {xd+=lr.offsetLeft; yd+=lr.offsetTop; lr=lr.offsetParent;}
			x=event.pageX-xd;
			y=event.pageY-yd;
		}
		setHSL(Math.round(x*255/127),Math.round(255-y*255/127),curhsl[2]);
	}
	
	function setFromSlider (event) {
		xd=0;lr=document.getElementById("slider");
		while(lr!=null) {xd+=lr.offsetLeft; lr=lr.offsetParent;}
		x=event.clientX-xd;
		setHSL(curhsl[0],curhsl[1],Math.round(255-x*255/127));
	}

    function applyColorChange() {
        colorNames=new Array();
        colorColors=new Array();    
        backendNames=new Array();
        backendColors=new Array();

        j=0
        for (i=0;i<paletteLength;i++){
                name = new String(paletteNames[i]);
                if (name!="") {
                    colorNames[j]=name;
                    colorColors[j]=paletteColors[i];
                    j++;
                }
                backendNames[i]=name;
                backendColors[i]=paletteColors[i];

        }

        document.getElementById('hiddenvalues').value=backendColors.toString();
        document.getElementById('hiddennames').value=backendNames.toString();


        // #b5056344# sorting the names hex values have to match the 
        // sort of the names
        lookupHex = new Array();
        for (i=0;i<colorNames.length;i++){
        lookupHex[colorNames[i]] = colorColors[i];
        }

        colorNames.sort();
        for (i=0;i<colorNames.length;i++){
            colorColors[i] = lookupHex[colorNames[i]];
        }
        self.opener.updateColorChoosers(colorNames, colorColors, localizedNoName);

        return true;
    }

    function addButton() {
        // Get the current color and the name
        var htmlcode = document.getElementById('EditColor.HtmlValue').value;
        var name = document.getElementById("EditColor.NameValue").value;
        // bugfix #b5039693# set these immediately as they take a while to
        // update in IE
        document.getElementById('imgCol'+cursorPos).src=blankImg.src;
        document.getElementById('defCol'+cursorPos).bgcolor="#"+htmlcode;
        // Save the name and color as variables so they can be gotten for
        // the page refresh
        theMasthead.originalColor=htmlcode;
        theMasthead.currentName=name;
        if (name=="") {
            name=htmlcode;
        }
        for(i=paletteLength; i < cursorPos; i++) {
            paletteColors[i]="";
            paletteNames[i]="";
        }                  
        paletteColors[cursorPos]='#'+htmlcode;
        paletteNames[cursorPos]=name;
        if (cursorPos>=paletteLength)
            paletteLength=cursorPos+1;
        updatePalette();
        setCursor(cursorPos);
        theMasthead.cursorPos=cursorPos;
    }

    function replaceButton() {
        var htmlcode = document.getElementById('EditColor.HtmlValue').value;
        var name = document.getElementById("EditColor.NameValue").value;
        // Save the name and color as variables so they can be gotten for
        // the page refresh
        theMasthead.originalColor=htmlcode;
        theMasthead.currentName=name;
        if (name=="") {
            name=htmlcode;
        }
        paletteColors[cursorPos]='#'+htmlcode;
        paletteNames[cursorPos]=name;
        updatePalette();
        setCursor(cursorPos);
        theMasthead.cursorPos=cursorPos;
    }

    function removeButton() {
        theMasthead.originalColor=document.getElementById('EditColor.HtmlValue').value;
        var alt = document.getElementById("imgCol"+cursorPos).alt;
        if (alt == ""){
            alert(removeText);
        } else {
            var colorToRemove = document.getElementById("defCol"+cursorPos).bgColor;
            paletteLength=paletteLength-1;
            var paletteRemovedColors=new Array(paletteLength);
            var paletteRemovedNames=new Array(paletteLength);
            for (i=0;i<paletteLength;i++){
                if (i<cursorPos){
                    paletteRemovedColors[i]=paletteColors[i];
                    paletteRemovedNames[i]=paletteNames[i];
                } else if (i>=cursorPos){
                    paletteRemovedColors[i]=paletteColors[i+1];
                    paletteRemovedNames[i]=paletteNames[i+1];
                }
            }
            paletteColors=paletteRemovedColors;
            paletteNames=paletteRemovedNames;
            updatePalette();
            setCursor(cursorPos);
            setCurrentColorTo(cursorPos);
        }
        return false;
    }

    function restoreOrigColorButton() {
        // Sets the original color (saved in the oldcol variable) to be the 
        // current color.
        theMasthead.currentName=origName;
        setCol(oldcol);
    }

    function restoreButton() {
        var colorNames = ["lightsalmon","salmon","redlight","red", 
                        "darkred","redbrown","redblack","black","lightlemon",
                        "lemon","yellowlight","yellow","darkyellow","yellowbrown",
                        "yellowblack","nearlyblack","lightlime","lime","greenlight",
                        "green","darkgreen","greenbrown","greenblack","lightestblack",
                        "lightbabyblue","babyblue","brightbluelight","brightblue",
                        "darkbrightblue","brightbluebrown","brightblueblack",
                        "darkgrey","lightpurpleblue","purpleblue","bluelight",
                        "blue","darkblue","bluebrown","blueblack","lightgrey",
                        "lightbabypink","babypink","pinklight","pink","darkpink",
                        "pinkbrown","pinkblack","white"];
        var colorNamesNoName = [localizedNoName,"lightsalmon","salmon","redlight","red", 
                        "darkred","redbrown","redblack","black","lightlemon",
                        "lemon","yellowlight","yellow","darkyellow","yellowbrown",
                        "yellowblack","nearlyblack","lightlime","lime","greenlight",
                        "green","darkgreen","greenbrown","greenblack","lightestblack",
                        "lightbabyblue","babyblue","brightbluelight","brightblue",
                        "darkbrightblue","brightbluebrown","brightblueblack",
                        "darkgrey","lightpurpleblue","purpleblue","bluelight",
                        "blue","darkblue","bluebrown","blueblack","lightgrey",
                        "lightbabypink","babypink","pinklight","pink","darkpink",
                        "pinkbrown","pinkblack","white"];
        var colorValues = ["#ffc0c0","#ff8080","#ff4040","#ff0000","#c00000",
                        "#800000","#400000","#000000","#ffffc0","#ffff80","#ffff40",
                        "#ffff00","#c0c000","#808000","#404000","#202020","#c0ffc0",
                        "#80ff80","#40ff40","#00ff00","#00c000","#008000","#004000",
                        "#404040","#c0ffff","#80ffff","#40ffff","#00ffff","#00c0c0",
                        "#008080","#004040","#808080","#c0c0ff","#8080ff","#4040ff",
                        "#0000ff","#0000c0","#000080","#000040","#c0c0c0","#ffc0ff",
                        "#ff80ff","#ff40ff","#ff00ff","#c000c0","#800080","#400040",
                        "#ffffff"];
        paletteNames=colorNames;
        paletteColors=colorValues;
        paletteLength=48;
        document.getElementById('hiddenvalues').value=paletteColors.toString();
        document.getElementById('hiddennames').value=paletteNames.toString();
        updatePalette();
        theMasthead.currentName="";
        theMasthead.originalColor=document.getElementById('EditColor.HtmlValue').value;
    }


    function updatePalette(){
        //Remove trailing empty values
        var runner = paletteLength-1;

        while (paletteNames[runner] == "") {
            runner--;
        }
        paletteNames.length = runner+1;
        paletteColors.length = runner+1;
        paletteLength = runner+1;
        for (i=0;i<paletteLength;i++){
            if(paletteNames[i] == "undefined") {
                paletteNames[i] = "";
            }
            elementTD = document.getElementById("defCol"+i);
            elementImg = document.getElementById("imgCol"+i);
            elementImg.alt=paletteNames[i];
            if ((elementImg.alt=="")){
                elementImg.alt="";
                elementTD.bgColor='#ffffff';   
                elementImg.src=xImg.src;
                elementTD.title="";
            } else {
                elementImg.src=blankImg.src;
                elementTD.bgColor=paletteColors[i];
                elementTD.title=paletteNames[i];
            }
            for (j=paletteLength; j<96; j ++) {
                elementTD = document.getElementById("defCol"+j);
                elementImg = document.getElementById("imgCol"+j);
                elementTD.bgColor='#ffffff';
                elementImg.src=xImg.src;
                elementImg.alt="";
                elementTD.title="";
            }
        }
    }


    function incrementColor(toIncrement) {
        document.getElementById("EditColor.NameValue").value="";
        if (toIncrement == "Red"){
            setRGB(parseInt(currgb[0]) + 1 + "", currgb[1], currgb[2]);
        } else if (toIncrement == "Green") {
            setRGB(currgb[0], parseInt(currgb[1]) + 1 + "", currgb[2]);
        } else if (toIncrement == "Blue") {
            setRGB(currgb[0], currgb[1], parseInt(currgb[2]) + 1 + "");
        } else if (toIncrement == "Hue") {
            setHSL(parseInt(curhsl[0]) + 1 + "", curhsl[1], curhsl[2]);
        } else if (toIncrement == "Saturation") {
            setHSL(curhsl[0], parseInt(curhsl[1]) + 1 + "", curhsl[2]);
        } else if (toIncrement == "Luminance") {
            setHSL(curhsl[0], curhsl[1], parseInt(curhsl[2]) + 1 + "");
        }
    }

    function decrementColor(toDecrement) {
        document.getElementById("EditColor.NameValue").value="";
        if (toDecrement == "Red"){
            setRGB(currgb[0]-1, currgb[1], currgb[2]);
        } else if (toDecrement == "Green") {
            setRGB(currgb[0], currgb[1]-1, currgb[2]);
        } else if (toDecrement == "Blue") {
            setRGB(currgb[0], currgb[1], currgb[2]-1);
        } else if (toDecrement == "Hue") {
            setHSL(curhsl[0]-1, curhsl[1], curhsl[2]);
        } else if (toDecrement == "Saturation") {
            setHSL(curhsl[0], curhsl[1]-1, curhsl[2]);
        } else if (toDecrement == "Luminance") {
            setHSL(curhsl[0], curhsl[1], curhsl[2]-1);
        }
    }

    function setCurrentColorTo(tileNumber) {
        tileColor=document.getElementById('defCol'+tileNumber).bgColor;
        tileColor=tileColor.substring(1);
        setCol(tileColor);
        setCursor(tileNumber);
        setName(tileNumber);
    }


    function setCurrentColorTo(tileNumber) {
        tileColor=document.getElementById('defCol'+tileNumber).bgColor;
        tileColor=tileColor.substring(1);
        setCol(tileColor);
        setCursor(tileNumber);
        setName(tileNumber);
    }

    function isColorNameUnique() {
        // Get the current color and the name
        var htmlcode = document.getElementById('EditColor.HtmlValue').value;
        var name = document.getElementById("EditColor.NameValue").value;
        var nameIsUnique = true;
        if (name=="") {
            name=htmlcode;
        }
        if (name == localizedNoName) {
            nameIsUnique = false;
        }   
        for (i=0; i < paletteNames.length; i++) {
            if (name == paletteNames[i]) {
                nameIsUnique = false;
                break;
            }
        }
        return nameIsUnique;
    }       

