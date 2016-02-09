$(document).ready(function(){
	var chart = dc.seriesChart("#Teste1");
	var ndx, dimensaoDaAmostra, totalDoTempo;

	var dataTest = data;
	var algorithm = [];
	var lastCode = -1;
	
	for( var i = 0 ; i < dataTest.length; i ++ ){
		console.log(!(dataTest[i].algorithmCode == lastCode));
		if(!(dataTest[i].algorithmCode == lastCode)){
			console.log("entrou no if")
			algorithm.push([dataTest[i].algorithmCode, dataTest[i].algorithm]);
			lastCode = dataTest[i].algorithmCode;
		}
	}
	
	console.log(algorithm);
	

    ndx = crossfilter(dataTest);
    dimensaoDaAmostra = ndx.dimension(function(d){ return [+d.algorithmCode, +d.xaxis]; });
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
    	.mouseZoomable(true)
    	.clipPadding(10)
    	.renderHorizontalGridLines(true)
    	.dimension(dimensaoDaAmostra)
    	.group(totalDoTempo)
    	.seriesAccessor(function(d) {
    		for(var i = 0; i < algorithm.length; i++){
    			if(d.key[0] == algorithm[i][0]){
    				return algorithm[i][1];
    			}
    		}
    	})
    	.keyAccessor(function(d) {return +d.key[1];})
    	.valueAccessor(function(d) {return +d.value;})
    	.legend(dc.legend().x(80).y(20).itemHeight(13).gap(5));
    
    dc.renderAll();
}); 