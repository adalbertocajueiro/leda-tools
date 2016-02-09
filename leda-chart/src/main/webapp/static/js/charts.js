$(document).ready(function(){
	var chart = dc.seriesChart("#Teste1");
	var ndx, dimensaoDaAmostra, totalDoTempo;

	var dataTest = data;

    ndx = crossfilter(dataTest);
    dimensaoDaAmostra = ndx.dimension(function(d){ return [+d.algorithm, +d.xaxis]; });
    totalDoTempo = dimensaoDaAmostra.group().reduceSum(function(d){return d.yaxis;});
    var menorTamanho = dimensaoDaAmostra.bottom(1)[0].xaxis;
    var maiorTamanho = dimensaoDaAmostra.top(1)[0].xaxis;
     console.log(maiorTamanho);
    chart
    	.width(768)
    	.height(480)
    	.chart(function(c) { return dc.lineChart(c).interpolate('basis'); })
    	.x(d3.scale.linear().domain([menorTamanho,maiorTamanho]))
    	.margins({top: 30, right: 50, bottom: 40, left: 40})
    	.yAxisLabel("Tempo em segundos")
    	.xAxisLabel("Tamanho da amostra")
    	.brushOn(false)
    	.renderHorizontalGridLines(true)
    	.dimension(dimensaoDaAmostra)
    	.group(totalDoTempo)
    	.seriesAccessor(function(d) {if(d.key[0] ==1){ return "Insertionsort";} else{ return"Bubblesort";}})
    	.keyAccessor(function(d) {return +d.key[1];})
    	.valueAccessor(function(d) {return +d.value;})
    	.legend(dc.legend().x(80).y(20).itemHeight(13).gap(5));
    
    dc.renderAll();
	/*
	var chart1 = dc.compositeChart("#Teste1");

    var dataTest = data;

    var ndx = crossfilter(dataTest);

    var dimensaoDaAmostra = ndx.dimension(function(d){ return d.xaxis;});
    var menorTamanho = dimensaoDaAmostra.bottom(1)[0].xaxis;
    var maiorTamanho = dimensaoDaAmostra.top(1)[0].xaxis;
   
    var all = ndx.group;

	chart1
    .width(768)
    .height(480)
    .margins({top: 30, right: 50, bottom: 40, left: 40})
    .dimension(dimensaoDaAmostra)
    .yAxisLabel("Tempo em segundos")
    .xAxisLabel("Tamanho da amostra")
    .legend(dc.legend().x(80).y(20).itemHeight(13).gap(5))
    .x(d3.scale.linear().domain([menorTamanho,maiorTamanho]))
    .brushOn(false) 
    .renderHorizontalGridLines(true);
    

    var i = 0;
    var auxType = dataTest[i].algorithm;
    var lastgroup = [];
    var groupList = [];

    var firstgroup = true;
    
    console.log(auxType);
    while( auxType == dataTest[i].algorithm ) { 

    	lastgroup.push(dataTest[i]);

        if(i < dataTest.length - 1) { 
            if(dataTest[i + 1].algorithm !== auxType){
                auxType = dataTest[i + 1].algorithm;
                console.log(dataTest[i].algorithm);
                createGroup(chart1, lastgroup);
                
                lastgroup = [];
                firstgroup = false;
            }
            console.log(i);
            i++;
        } 
        else {
        	console.log(dataTest[i].algorithm)
            createGroup(chart1, lastgroup);
            auxType = "parou";
        }
    }
    chart1.compose(groupList);

    function createGroup(chart1, lastgroup) {
    	var ndx = crossfilter(lastgroup);
        var dimensaoDaAmostra = ndx.dimension(function(d){ return d.xaxis;});
        var totalDoTempo = dimensaoDaAmostra.group().reduceSum(function(d){return d.yaxis;});
    	groupList.push(dc.lineChart().group(totalDoTempo, lastgroup[0].algorithm));  
            
    }

    dc.renderAll();*/
}); 