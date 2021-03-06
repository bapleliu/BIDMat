package BIDMat
import Mat._

case class CSMat(dims0:Array[Int], val data:Array[String]) extends DenseMat[String](dims0, data) {	
  
  def this(nr:Int, nc:Int, data:Array[String]) = this(Array(nr, nc), data);
 
	override def t:CSMat = CSMat(gt(null))
	
	override def mytype = "CSMat"
	
	def horzcat(b: CSMat) = CSMat(ghorzcat(b))
	
	def vertcat(b: CSMat) = CSMat(gvertcat(b))
	
	def find3:(IMat, IMat, CSMat) = { val vv = gfind3 ; (IMat(vv._1), IMat(vv._2), CSMat(vv._3)) }
	
	override def apply(a:IMat):CSMat = CSMat(gapply(a))
	
	override def apply(a:IMat, b:IMat):CSMat = CSMat(gapply(a, b))	
	
	override def apply(a:Int, b:IMat):CSMat = CSMat(gapply(a, b))	
		
	override def apply(a:IMat, b:Int):CSMat = CSMat(gapply(a, b))	
  
  
  def update(i:Int, b:String):String = _update(i, b)
  
  def update(i:Int, j:Int, b:String):String = _update(i, j, b)
  
  
  def update(iv:IMat, b:CSMat):CSMat = CSMat(_update(iv, b))
  
  def update(iv:IMat, jv:IMat, b:CSMat):CSMat = CSMat(_update(iv, jv, b))

  def update(iv:IMat, j:Int, b:CSMat):CSMat = CSMat(_update(iv, IMat.ielem(j), b))

  def update(i:Int, jv:IMat, b:CSMat):CSMat = CSMat(_update(IMat.ielem(i), jv, b))
  
//  override def update(inds:IMat, b:Int):Mat = CSMat(_update(inds, b.toFloat))
  
//  override def update(inds:IMat, b:Float):Mat = CSMat(_update(inds, b))
  
  override def update(iv:IMat, b:Mat):CSMat = CSMat(_update(iv, b.asInstanceOf[CSMat]))
  
  override def update(iv:IMat, jv:IMat, b:Mat):CSMat = CSMat(_update(iv, jv, b.asInstanceOf[CSMat]))

  override def update(iv:IMat, j:Int, b:Mat):CSMat = CSMat(_update(iv, IMat.ielem(j), b.asInstanceOf[CSMat]))

  override def update(i:Int, jv:IMat, b:Mat):CSMat = CSMat(_update(IMat.ielem(i), jv, b.asInstanceOf[CSMat]))
  
  
  def update(iv:Mat, b:String):CSMat = CSMat(_update(iv.asInstanceOf[IMat], b))
  
  def update(iv:Mat, jv:Mat, b:String):CSMat = CSMat(_update(iv.asInstanceOf[IMat], jv.asInstanceOf[IMat], b))

  def update(iv:Mat, j:Int, b:String):CSMat = CSMat(_update(iv.asInstanceOf[IMat], IMat.ielem(j), b))

  def update(i:Int, jv:Mat, b:String):CSMat = CSMat(_update(IMat.ielem(i), jv.asInstanceOf[IMat], b))
  
	def ccMatOp(b: CSMat, f:(String, String) => String, old:CSMat) = CSMat(ggMatOp(b, f, old))
	
	def ccMatOpScalar(b: String, f:(String, String) => String, old:CSMat) = CSMat(ggMatOpScalar(b, f, old))
	
	def ccReduceOp(n:Int, f1:(String) => String, f2:(String, String) => String, old:CSMat) = CSMat(ggReduceOp(n, f1, f2, old))
	
	override def printOne(i:Int):String = {
	  val v = _data(i)
	  if (v != null)
		  v.toString()
		else	
		  "NULL"
	}
	
	def toIMat:IMat = {
	  val out = IMat.newOrCheckIMat(nrows, ncols, null, GUID, "CSMat.toIMat".##);
	  var i = 0;
	  while (i < length) {
	    out.data(i) = data(i).toInt;
	    i += 1;
	  }
	  out
	}
	
  def toFMat:FMat = {
	  val out = FMat.newOrCheckFMat(nrows, ncols, null, GUID, "CSMat.toFMat".##);
	  var i = 0;
	  while (i < length) {
	    out.data(i) = data(i).toFloat;
	    i += 1;
	  }
	  out;
	}
  
  def toDMat:DMat = {
	  val out = DMat.newOrCheckDMat(nrows, ncols, null, GUID, "CSMat.toDMat".##);
	  var i = 0;
	  while (i < length) {
	    out.data(i) = data(i).toDouble;
	    i += 1;
	  }
	  out;
	}
  
  def toLMat:LMat = {
	  val out = LMat.newOrCheckLMat(nrows, ncols, null, GUID, "CSMat.toLMat".##);
	  var i = 0;
	  while (i < length) {
	    out.data(i) = data(i).toLong;
	    i += 1;
	  }
	  out;
	}
	
	/* 
	 * Trait to implement binary operations on dense matrices
	 */
	trait DCSMatOp {
		@inline def op1(x:String):String = x;
		def op2(x:String, y:String):String;

		def dCSMatOp(a:CSMat):CSMat = 
			if (nrows==a.nrows && ncols==1) {
				val out = CSMat(nrows, a.ncols)
				for (i <- 0 until a.ncols) {
					for (j <- 0 until nrows) {
						out.data(j+i*nrows) = op2(data(j), a.data(j+i*a.nrows))
					}
				}
				out
			} else if (ncols==a.ncols && nrows==1) {
				val out = CSMat(a.nrows, ncols)
				for (i <- 0 until ncols) {
					for (j <- 0 until a.nrows) {
						out.data(j+i*a.nrows) = op2(data(i), a.data(j+i*a.nrows))
					}
				}
				out
			} else if (nrows==a.nrows && a.ncols==1) {
				val out = CSMat(nrows, ncols)
				for (i <- 0 until ncols) {
					for (j <- 0 until nrows) {
						out.data(j+i*nrows) = op2(data(j+i*nrows), a.data(j))
					}
				}
				out
			} else if (ncols==a.ncols && a.nrows==1) {
				val out = CSMat(nrows, ncols)
				for (i <- 0 until ncols) {
					for (j <- 0 until nrows) {
						out.data(j+i*nrows) = op2(data(j+i*nrows), a.data(i))
					}
				}
				out
			} else dCSMatOpStrict(a)
		
		def dCSMatOpStrict(a:CSMat):CSMat = 
			if (nrows==a.nrows && ncols==a.ncols) {
				val out = CSMat(nrows, ncols)
				var i = 0
				while (i < a.length) {
					out.data(i) = op2(data(i), a.data(i))
					i += 1
				}
				out
			} else if (a.nrows == 1 && a.ncols == 1) {
				val out = CSMat(nrows, ncols)
				val aval = a.data(0)
				for (i <- 0 until length) {
					out.data(i) = op2(data(i), aval)
				}
				out
			} else if (nrows == 1 && ncols == 1) {
				val out = CSMat(a.nrows, a.ncols)
				val aval = data(0)
				for (i <- 0 until a.length) {
					out.data(i) = op2(aval, a.data(i))
				}
				out
			} else throw new RuntimeException("dims incompatible")

		def dCSMatReduceOp(dim:Int):CSMat = 
		  if (dim == 1) {
		    val out = CSMat(1, ncols)
		    for (i <- 0 until ncols) { 
		      var j = 1
		      var acc = op1(data(i*nrows))
		      while (j < nrows) { 
			acc = op2(acc, data(j+i*nrows))
			j += 1
		      }
		      out.data(i) = acc
		    }
		    out
		  } else if (dim == 2) { 
		    val out = CSMat(nrows, 1)
		    var j = 0
		    while (j < nrows) { 
		      out.data(j) = op1(data(j))
		      j += 1
		    }
		    for (i <- 1 until ncols) { 
		      var j = 0
		      while (j < nrows) { 
			out.data(j) = op2(out.data(j), data(j+i*nrows))
			j += 1
		      }
		    }
		    out
		  } else
		    throw new RuntimeException("index must 1 or 2")			    
	}
	
	def kron(b: CSMat):CSMat = {
	  val out = CSMat(nrows * b.nrows, ncols * b.ncols)
	  var i = 0 
	  while (i < nrows) {
	    var j = 0 
	    while (j < b.nrows) {
	      var k = 0 
	      while (k < ncols){
	        var m = 0 
	        while (m < b.ncols) {
	          out.data(j + b.nrows*(i + nrows*(m + b.ncols*k))) = data(i + k*nrows) + b.data(j + m*b.nrows)
	          m += 1
	        }
	        k += 1
	      }
	      j += 1	      
	    }
	    i += 1
	  }
	  out
	}
	
  def + (b : CSMat) = ccMatOp(b, (x:String, y:String) => x + y, null)
  def ** (b : CSMat) = kron(b)
	def ⊗ (b : CSMat) = kron(b)
	
	def \ (b: CSMat) = horzcat(b)
	def \ (b: String) = horzcat(CSMat.cselem(b))
	def on (b: CSMat) = vertcat(b)
	def on (b: String) = vertcat(CSMat.cselem(b))
}

object CSMat {
  
    def apply(nr:Int, nc:Int):CSMat = new CSMat(nr, nc, new Array[String](nr*nc))

    def apply(a:DenseMat[String]):CSMat = new CSMat(a.nrows, a.ncols, a._data) 
    
    def apply(a:SBMat) = a.toCSMat
    
    def apply(a:List[String]) = new CSMat(1, a.length, a.toArray)
    
    def apply(a:FMat) = {
      new CSMat(a.dims.data, a.data.map(_.toString));
    }
    
    def apply(a:DMat) = {
      new CSMat(a.dims.data, a.data.map(_.toString));
    }
    
    def apply(a:IMat) = {
      new CSMat(a.dims.data, a.data.map(_.toString));
    }
        
    def apply(a:LMat) = {
      new CSMat(a.dims.data, a.data.map(_.toString));
    }
    
    def newOrCheckCSMat(nr:Int, nc:Int, outmat:Mat):CSMat = {
    if (outmat.asInstanceOf[AnyRef] == null || (outmat.nrows == 0 && outmat.ncols == 0)) {
      CSMat(nr, nc)
    } else {
      if (outmat.nrows != nr || outmat.ncols != nc) {
        outmat.recycle(nr, nc, 0).asInstanceOf[CSMat]
      } else {
      	outmat.asInstanceOf[CSMat]
      }
    }
  }
    
    def cselem(x:String) = {
    	val out = CSMat(1,1)
    	out.data(0) = x
    	out
	}

}






