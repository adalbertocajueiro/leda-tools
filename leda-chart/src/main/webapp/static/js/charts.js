$(document).ready(function(){
	var chart1 = dc.lineChart("#Teste1");

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
    .yAxisLabel("Tempo")
    .xAxisLabel("Tamanho da amostra")
    .legend(dc.legend().x(80).y(20).itemHeight(13).gap(5))
    .x(d3.scale.linear().domain([menorTamanho,maiorTamanho]))
    .brushOn(false) 
    .renderHorizontalGridLines(true);
    

    var i = 0;
    var auxType = dataTest[i].algorithm;
    var lastgroup = [];
    var firstgroup = true;

    console.log(dataTest.length);
    while( auxType == dataTest[i].algorithm ) { 

        lastgroup.push(dataTest[i]);

        if(i < dataTest.length - 1) { 
            if(dataTest[i + 1].algorithm !== auxType){
                auxType = dataTest[i + 1].algorithm;
                console.log(dataTest[i+1].algorithm);
                createGroup(lastgroup, chart1, firstgroup);

                lastgroup = [];
                firstgroup = false;
            }
            console.log(i);
            i++;
            
        } 
        else {
            createGroup(lastgroup, chart1);
            auxType = "parou";
        }

        
    }

    function createGroup(newData, chart1, firstgroup) {
        var ndx = crossfilter(newData);
        var dimensaoDaAmostra = ndx.dimension(function(d){ return d.xaxis;});
        var totalDoTempo = dimensaoDaAmostra.group().reduceSum(function(d){return d.yaxis;});

        if (firstgroup) {
            chart1.group(totalDoTempo, newData[0].algorithm);
        }
        else {
            chart1.stack(totalDoTempo, newData[0].algorithm);
        }
    }

    dc.renderAll();
}); 