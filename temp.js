var chngOrder = function( a ){
    for( var i=0; i<a.length-1; i++ ){
        var j = (i === 0) ? a.length-1 : i-1;
        var temp = a[j];
        a[j] = a[i];
        a[i] = temp;
    }
    return a;
};

console.log(chngOrder[1,2,3,4]));
