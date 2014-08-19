
function stripTable() {
    var tables = document.getElementsByClassName('table striped');//|getElementsByClass(document, 'table striped');
    for (t = 0; t < tables.length; t++) {
        var rows = tables[t].getElementsByTagName('tr');
        if (typeof rows[0] != 'undefined') {
            addClass(rows[0], 'strip-header');
            //strip the table....
        }
        for (i = 1; i < rows.length; i++) {
            if (i % 2 === 0) {
                //even row
                addClass(rows[i], 'strip-even');
            } else {
                //odd row
                addClass(rows[i], 'strip-odd');
            }
        }
    }
}
function showTab(tab) {
    removeClass(tab, 'hidden');
}
function hideTab(tab) {
    addClass(tab, 'hidden');
}
function showTabListner(event) {
    var div = document.getElementById(this.link_for);
//    alert(div.innerHTML);
    hideAllTabs(this.parentNode);
    if (hasClass(div, 'hidden')) {
        showTab(div);
        addClass(this, "active");
    }
    event.preventDefault(event);
}
function prepareAllTabs(start) {
    start = start || 0;
    var tabbed = document.getElementsByClassName('tabbed');
    for (i = 0; i < tabbed.length; i++) {
        var folder = tabbed[i];
        prepareTab2(folder,start);
    }
}
function prepareTab2(folder,start) {
    var tabs = folder.getElementsByClassName('tab');
    var link_cont = document.createElement("div");
    addClass(link_cont, "centered");
    addClass(link_cont, "link_container_div");
    document.getElementById('tabs_link_container').appendChild(link_cont);
    var first;
    for (j = 0; j < tabs.length; j++) {
        var button = document.createElement('a');
        var text = document.createTextNode(tabs[j].title);
        button.appendChild(text);
        tabs[j].id = 'tab' + j;
        tabs[j].className += ' hidden';
        button.href = '#tab' + j;
        button.link_for = 'tab' + j;
        button.className = "tab_link";
        button.addEventListener('click', showTabListner, true);
        if (j === start) {
            first = button;
        }
        link_cont.appendChild(button);
    }
    hideAllTabs(folder);
    addClass(first, 'active');
    showTab(tabs[start])

}
function hideAllTabs(folder) {
    var tabs = document.getElementsByClassName('tab');
    for (var i = 0; i < tabs.length; i++) {
        var tab = tabs[i];
//        alert(tab.innerHTML);
        hideTab(tabs[i]);
    }

    var links = document.getElementById('tabs_link_container').getElementsByClassName("active")[0];
    removeClass(links, 'active')
}
function removeClass(target, className) {
    if (typeof className == 'undefined' || typeof target == 'undefined') {
//        alert('error....');
        return;
    }
    var pattern = new RegExp('(^| )' + className + '( |$)');
    target.className = target.className.replace(pattern, '$1');
    target.className = target.className.replace(/ $/, '');
}
function addClass(target, className) {
    if (hasClass(target, className)) {
        return;
    }
    else if (!hasAnyClass(target)) {
        target.className = className;
    }
    else {
        target.className += ' ' + className;
    }
}
function hasClass(target, className) {
    if (typeof className == 'undefined') {
        alert('error....');
        return false;
    }
    var pattern = new RegExp('(^| )' + className + '( |$)');
    if (pattern.test(target.className)) {
        return true;
    }
    return false;
}
function hasAnyClass(target) {
    if (target.className.trim() == '') {
        return false;
    }
    return true;
}

function getElementsByClass(target, className) {
    var pattern = new RegExp('(^| )' + className + '( |$)');
    var allElements = target.getElementsByTagName('*');
    var result = [
    ];
    for (i = 0; i < allElements.length; i++) {
        if (pattern.test(allElements[i].className)) {
            result[result.length] = allElements[i];
        }
    }
    return result;
}
