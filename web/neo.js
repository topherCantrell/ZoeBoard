var NEO = (function() {
	
	/*
	 
	 Pixels are SVG elements: <circle id='?_?>
	    where the first ? is the name of the strip and the second ? is the pixel number
	   
	 Pixels can be arranged on the page in many (or one) <svg> element.	
	 
	 TODO talk about the cursor and pixel-creating
	 
	 */	
	
	var my = {};
	
	/**
	 * Set the color of a particular pixel.
	 * @param strip the strip name
	 * @param pix the pixel number
	 * @param color the new color
	 */
	my.setPixel = function(strip,pix,color) {
		$("#"+strip+"_"+pix).attr("fill",color);	
	}	
	
	var OFFS = [[0,-1],[1,0],[0,1],[-1,0]];	
	my.cursor = {}	
	
    /**
     * Configure the pixel-drawing cursor.
     * @param x the x coordinate of the cursor
     * @param y the y coordinate of the cursor
     * @param strip the target SVG id to append to
     * @param radius the radius of the pixel
     * @param gap the distance between pixel centers
     * @param number the starting pixel number for the cursor
     */	
	my.setCursor = function(x,y,svg,strip,radius,gap,number) {		
		my.cursor.x = x;
		my.cursor.y = y;
		if(svg!==undefined) my.cursor.svg = svg;
		if(strip!==undefined) my.cursor.strip = strip;
		if(radius!==undefined) my.cursor.radius = radius;
		if(gap!==undefined) my.cursor.gap = gap;
		if(number!==undefined) my.cursor.number = number;
	};
	
	/**
	 * Make a line of pixels at the pixel-drawing cursor
	 * @param direction the direction of the line (0,1,2, or 3)
	 * @param length number of pixels to make
	 */
	my.makeLine = function(direction,length) {			
		var st = $("#"+my.cursor.svg);
		var ox = OFFS[direction][0];
		var oy = OFFS[direction][1];	
		for(var z=0;z<length;++z) {		
			st.append("<circle "+
					"id='"+(my.cursor.strip)+"_"+(my.cursor.number)+"' "+
					"cx='"+(my.cursor.x)+"' "+
					"cy='"+(my.cursor.y)+"' "+
					"r='"+my.cursor.radius+"' "+
					"stroke='#E0E0E0' stroke-width='1' "+
					"fill='#F8F8F8'/>");
			my.cursor.x += ox*my.cursor.gap;
			my.cursor.y += oy*my.cursor.gap;
			++my.cursor.number;
		}
		$("#"+my.cursor.svg).html($("#"+my.cursor.svg).html());
	};		
			
	return my;

}());
