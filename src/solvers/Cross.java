package solvers;

public class Cross {
	private static short[][] fmv=new short[7920][6],pmv=new short[11880][6];
	private static byte[] permPrun=new byte[11880],flipPrun=new byte[7920];
	private static byte[][] fcm = new byte[24][6];
	private static byte[][] fem = new byte[24][6];
	private static byte[][] fecd = new byte[4][576];
	private static int[] goalCo = { 12, 15, 18, 21 };
	private static int[] goalFeo = { 0, 2, 4, 6 };
	private static StringBuffer sb;
	public static boolean inic=false, inix=false;
	private static String[] color={"D: ","U: ","L: ","R: ","F: ","B: "};
	private static String[][] moveIdx={{"UDLRFB","DURLFB","RLUDFB","LRDUFB","BFLRUD","FBLRDU"},
		{"UDLRFB","DURLFB","RLUDFB","LRDUFB","BFRLDU","FBRLUD"},
		{"UDLRFB","DURLFB","RLUDFB","LRDUFB","BFUDRL","FBUDLR"},
		{"UDLRFB","DURLFB","RLUDFB","LRDUFB","BFDULR","FBDURL"},
		{"UDLRFB","DULRBF","RLBFUD","LRFBUD","BFLRUD","FBRLUD"},
		{"UDLRFB","DULRBF","RLFBDU","LRBFDU","BFRLDU","FBLRDU"}};
	private static String[][] rotIdx={{"","z2","z'","z","x'","x"},{"z2","","z","z'","x","x'"},
		{"z","z'","","z2","y","y'"},{"z'","z","z2","","y'","y"},
		{"x","x'","y'","y","","y2"},{"x'","x","y","y'","y2",""}};
	private static String[][] turn={{"U","D","L","R","F","B"},{"D","U","R","L","F","B"},
		{"R","L","U","D","F","B"},{"L","R","D","U","F","B"},
		{"B","F","L","R","U","D"},{"F","B","L","R","D","U"}};
	private static String[] suff={"","2","'"};
	public static void circle(int[] d, int f,int h,int l,int n,int s){
		int q=d[f];d[f]=d[n]^s;d[n]=d[l]^s;d[l]=d[h]^s;d[h]=q^s;
	}
	private static int getmv(int c,int p, int o,int f){
		int[] n=new int[12], s=new int[4];
		int q,t,v;
		for(q=1;4>=q;q++){
			t=p%q;
			for(p=p/q,v=q-2;v>=t;v--)
				s[v+1]=s[v];s[t]=4-q;
		}
		q=4;
		for(t=0;12>t;t++)
			if(c>=Tl.Cnk[11-t][q]){
				c-=Tl.Cnk[11-t][q--];
				n[t]=s[q]<<1|o&1;
				o>>=1;
			}
			else n[t]=-1;
		switch(f){
		case 0:
			circle(n,0,1,2,3,0);break;
		case 1:
			circle(n,11,10,9,8,0);break;
		case 2:
			circle(n,1,4,9,5,0);break;
		case 3:
			circle(n,3,6,11,7,0);break;
		case 4:
			circle(n,0,7,8,4,1);break;
		case 5:
			circle(n,2,5,10,6,1);break;
		}
		c=0;q=4;
		for(t=0;12>t;t++)
			if(0<=n[t]){
				c+=Tl.Cnk[11-t][q--];
				s[q]=n[t]>>1;
			o|=(n[t]&1)<<3-q;
			}
		int i=0;
		for(q=0;4>q;q++){
			for(v=t=0;4>v&&!(s[v]==q);v++)if(s[v]>q)t++;
			i=i*(4-q)+t;
		}
		return 24*c+i<<4|o;
	}
	private static void initc(){
		if(inic)return;
		int i,j,D,y,C;
		//Tl.init();
		for(i=0;495>i;i++)
			for(j=0;24>j;j++){
				for(int s=0;6>s;s++){
					D=getmv(i,j,j,s);
					pmv[24*i+j][s]=(short) (D>>4);
					if(16>j)fmv[16*i+j][s]=(short) (((D>>4)/24)<<4|D&15);
				}
			}
		for(i=0;11880>i;i++)permPrun[i]=-1;
		permPrun[0]=0;i=1;
		for(j=0;5>=j;j++)
			for(D=0;11880>D;D++)
				if(permPrun[D]==j)
					for(int s=0;6>s;s++)
						for(y=D,C=0;3>C;C++){
							y=pmv[y][s];
							if(-1==permPrun[y]){permPrun[y]=(byte) (j+1);i++;}
						}
		for(i=0;7920>i;i++)flipPrun[i]=-1;
		flipPrun[0]=0;i=1;
		for(j=0;6>=j;j++)
			for(D=0;7920>D;D++)
				if(flipPrun[D]==j)
					for(int s=0;6>s;s++){
						y=D;
						for(C=0;3>C;C++){
							y=fmv[y][s];
							if(-1==flipPrun[y])flipPrun[y]=(byte) (j+1);
							i++;
						}
					}
		inic=true;
	}
	private static void initx(){
		if(inix)return;
		initc();
		byte[][] p = {
				{1,0,3,0,0,4},{2,1,1,5,1,0},{3,2,2,1,6,2},{0,3,7,3,2,3},
				{4,7,0,4,4,5},{5,4,5,6,5,1},{6,5,6,2,7,6},{7,6,4,7,3,7}
		};
		byte[][] o = {
				{0,0,1,0,0,2},{0,0,0,2,0,1},{0,0,0,1,2,0},{0,0,2,0,1,0},
				{0,0,2,0,0,1},{0,0,0,1,0,2},{0,0,0,2,1,0},{0,0,1,0,2,0}
		};
		for(int i=0; i<8; i++)
			for(int j=0; j<3; j++)
				for(int k=0; k<6; k++)
					fcm[i*3+j][k] = (byte) (p[i][k]*3+(o[i][k]+j)%3);
		p = new byte[][] {
				{0,0,7,0,0,8},{1,1,1,9,1,4},{2,2,2,5,10,2},{3,3,11,3,6,3},
				{5,4,4,4,4,0},{6,5,5,1,5,5},{7,6,6,6,2,6},{4,7,3,7,7,7},
				{8,11,8,8,8,1},{9,8,9,2,9,9},{10,9,10,10,3,10},{11,10,0,11,11,11}
		};
		o = new byte[][] {
				{0,0,0,0,0,1},{0,0,0,0,0,1},{0,0,0,0,1,0},{0,0,0,0,1,0},
				{0,0,0,0,0,1},{0,0,0,0,0,0},{0,0,0,0,1,0},{0,0,0,0,0,0},
				{0,0,0,0,0,1},{0,0,0,0,0,0},{0,0,0,0,1,0},{0,0,0,0,0,0}
		};
		for (int i = 0; i < 12; i++)
			for (int j = 0; j < 2; j++)
				for (int k = 0; k < 6; k++)
					fem[i * 2 + j][k] = (byte) (p[i][k]*2+(o[i][k]^j));
		for(int idx=0; idx<4; idx++){
			for(int i=0; i<576; i++)fecd[idx][i]=-1;
			fecd[idx][idx*51+12]=0;
			for(int d=0; d<6; d++)
				for(int i=0; i<576; i++)
					if(fecd[idx][i]==d)
						for(int j=0; j<6; j++)
							for(int y=i,k=0; k<3; k++){
								y=24*fem[y/24][j]+fcm[y%24][j];
								if(fecd[idx][y]==-1)
									fecd[idx][y]=(byte)(d+1);
							}
		}
		inix=true;
	}
	private static boolean idacross(int d,int h,int u,int w,int face){
        if(0==u)return 0==d&&0==h;
        if(permPrun[d]>u||flipPrun[h]>u)return false;
        int y,s,D,C;
        for(C=0;6>C;C++)
        	if(C!=w){
        		y=d;s=h;
        		for(D=0;3>D;D++) {
        			y=pmv[y][C];s=fmv[s][C];
        			if(idacross(y,s,u-1,C,face)){
        				sb.insert(0, " "+turn[face][C]+suff[D]);
        				return true;
        			}
        		}
        	}
        return false;
	}
	private static boolean idaxcross(int ep, int eo, int co, int feo, int idx, int depth, int l) {
		if (depth == 0)
			return ep == 0 && eo == 0 && co == goalCo[idx] && feo == goalFeo[idx];
		if (permPrun[ep] > depth || flipPrun[eo] > depth || fecd[idx][feo*24+co] > depth) return false;
		for (int i = 0; i < 6; i++)
			if (i != l) {
				int w = co, y = ep, s = eo, t = feo;
				for (int j = 0; j < 3; j++) {
					w = fcm[w][i]; t = fem[t][i];
					y = pmv[y][i]; s = fmv[s][i];
					if (idaxcross(y, s, w, t, idx, depth - 1, i)) {
						sb.insert(0, " " + turn[0][i] + suff[j]);
						return true;
					}
				}
			}
		return false;
	}
	
	public static String cross(String q2, int face, int side) {
		initc();
		String[] q=q2.split(" ");
		if(side==6){
			StringBuffer cross=new StringBuffer();
			for(int i=0;i<6;i++)cross.append(cross(q2, face, i));
			return cross.toString();
		}
		int s,D,C;
		for(s=0,D=0,C=0;C<q.length;C++)
			if(0!=q[C].length()){
				int o=moveIdx[face][side].indexOf(q[C].charAt(0));
				s=fmv[s][o]; D=pmv[D][o];
				if(1<q[C].length()) {
					if(q[C].charAt(1)=='2'){s=fmv[s][o];D=pmv[D][o];}
					else {s=fmv[fmv[s][o]][o];D=pmv[pmv[D][o]][o];}
				}
			}
		sb=new StringBuffer();
		for(C=0;9>C&&!idacross(D,s,C,-1,face);C++);
		return "\n"+color[side]+rotIdx[face][side]+sb.toString();
	}
	public static String solve(String s, int face) {
		String[] scr = s.split(" ");
		int[] co = new int[4], feo = new int[4];
		for (int i = 0; i < 4; i++) {co[i] = goalCo[i];feo[i] = goalFeo[i];}
		int ep = 0, eo = 0;
		for (int d = 0; d < scr.length; d++)
			if (0 != scr[d].length()) {
				int o = moveIdx[0][face].indexOf(scr[d].charAt(0));
				for (int i = 0; i < 4; i++) {co[i] = fcm[co[i]][o]; feo[i] = fem[feo[i]][o];}
				ep = pmv[ep][o]; eo = fmv[eo][o];
				if (1 < scr[d].length()) {
					if (scr[d].charAt(1) == '2') {
						for (int i = 0; i < 4; i++) {co[i] = fcm[co[i]][o]; feo[i] = fem[feo[i]][o];}
						eo = fmv[eo][o]; ep = pmv[ep][o];
					} else {
						for (int i = 0; i < 4; i++) {co[i] = fcm[fcm[co[i]][o]][o];feo[i] = fem[fem[feo[i]][o]][o];}
						eo = fmv[fmv[eo][o]][o]; ep = pmv[pmv[ep][o]][o];
					}
				}
			}
		sb = new StringBuffer();
		for (int d = 0; ; d++)
			for (int idx = 0; idx < 4; idx++)
				if (idaxcross(ep, eo, co[idx], feo[idx], idx, d, -1))
					return "\n" + color[face] + rotIdx[0][face] + sb.toString();
	}
	public static String xcross(String scr, int face) {
		initx();
		if (face < 6) return solve(scr, face);
		StringBuffer s = new StringBuffer();
		for (int i = 0; i < 6; i++) s.append(solve(scr, i));
		return s.toString();
	}
}