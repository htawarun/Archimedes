//const
PICK_PREFIX = "popupPick_"; //WARNING: code in VSPagePDXSuper.requestReceived relies on this naming convention
PARENT_PREFIX = "parentOf_";

function showPopupPick(form, hyperlink, width, height) 
{
	//if the pick is enabled...
	if(hyperlink.href)
	{
		//create pick window name (each caller gets one exclusive pick window)
		milliSeconds = new Date().getTime();
		pickWindowName = PICK_PREFIX + form.page_id.value + "_" + milliSeconds;
		
		//ensure parent has name (won't clobber existing name)
		if (window.name == null || window.name.length == 0)
		{
			window.name = PARENT_PREFIX + pickWindowName;
		}
		
		//open a popup window
		popupWin = window.open("", pickWindowName, "personalbar=no,toolbar=no,status=no,scrollbars=yes,resizable=yes,menubar=no,width=" + width + ",height=" + height);
		
		//give the popup window the focus
		popupWin.focus();
		
		//set vars on the window (not local to this function) so they persist after function exit
		window.form = form;
		window.arg = extractArgFromHyperlinkHREF(hyperlink.href);
		window.pickWindowName = pickWindowName;
		
		//set a timeout to submit the pick query (to ensure that the popup window is created first)
		//NOTE: this does not fire before this function exits, regardless of timeout value
		setTimeout("altSubmitForm(form, arg, pickWindowName);", 100);

		//prevent the hyperlink from activating, since we overrode its behaviour.
		return false;
	}
}

function altSubmitForm(form, arg, tar) {
	var oldTarget = form.target;
	var oldFrame = form.frame_name.value;
	var oldQryString = form.query_string.value;
	form.query_string.value = arg;
	form.target = tar;
	form.frame_name.value = "_self";
	form.submit();
	if ((navigator.userAgent.indexOf("MSIE")!=-1)) {               
		reset(form, oldTarget, oldFrame, oldQryString);
	} else {
		setTimeout(reset, 10, form, oldTarget, oldFrame, oldQryString);
	}
}

function closePickWindow(form, hyperlink) 
{        
	//if this window is a popup pick...
	if (window.name.indexOf(PICK_PREFIX) == 0)
	{
		//retarget the pick's result to the main window
		arg = extractArgFromHyperlinkHREF(hyperlink.href);

		//set the hyperlink to close the popup window
		hyperlink.href = "javascript:self.close();";

		//submit (targeted at the parent window)
		altSubmitForm(form, arg, opener.name);

		return true;
	}
}

function refreshAndCloseIfInPopup()
{		
	//if this window is a popup pick...
	if (window.name.indexOf(PICK_PREFIX) == 0) 
	{
		//resubmit parent's form with original frame name
		form = opener.document.forms[0];
		opener.submitForm(form, "", form.frame_name.value);
		
		//close self (the pick)
		setInterval('self.close()', 10);
	}
}

function extractArgFromHyperlinkHREF(hyperlinkHREF)
{
	argArray = hyperlinkHREF.split(",");
	arg = argArray[1];
	arg = arg.substring(1,arg.length-1);
	
	return arg;
}

function showAnyPDXPopupError()
{
	if (document.forms[0].pdxPopupErrorString != null) {
		alert(document.forms[0].pdxPopupErrorString.defaultValue);
	}
}
