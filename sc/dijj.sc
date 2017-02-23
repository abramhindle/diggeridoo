s.boot;

SynthDef(\pipe, {
	arg out=0,
	in=0,
	base=44.94,//10 to 500
	seperation=123.7,//10 to 200
	resonance=0.5,
	amp=0.5,
	q=1,
	dry=0,
	c=1.414;
	var freq1=base,
	freq2=freq1+(seperation),
	freq3=freq2+(c*seperation),
	freq4=freq3+(c*c*seperation),
	freq5=freq4+(c*c*c*seperation),
	freq6=freq5+(c*c*c*c*seperation),
	freq7=freq6+(c*c*c*c*c*seperation),
	freq8=freq7+(c*c*c*c*c*c*seperation),
	ina=In.ar(in);
	Out.ar(out,
			(amp*(
				BPF.ar(ina,freq1,q) +
				BPF.ar(ina,freq2,q) +
				BPF.ar(ina,freq3,q) +
				BPF.ar(ina,freq4,q) +
				BPF.ar(ina,freq5,q) +
				BPF.ar(ina,freq6,q) +
				BPF.ar(ina,freq7,q) +
				BPF.ar(ina,freq8,q)
			))/8.0
	)	
}).load(s);


SynthDef(\combpipe, {
	arg out=0,
	in=0,
	base=44.94,//10 to 500
	seperation=123.7,//10 to 200
	resonance=0.5,
	amp=0.5,
	q=1,
	dry=0,
	c=1.414;
	var freq1=base,
	freq2=base+1*seperation,
	freq3=base+2*seperation,
	freq4=base+3*seperation,
	freq5=base+4*seperation,
	freq6=base+5*seperation,
	freq7=base+6*seperation,
	freq8=base+7*seperation,
	ina=In.ar(in);
	Out.ar(out,
			(amp*(
				BPF.ar(ina,freq1,q) +
				BPF.ar(ina,freq2,q) +
				BPF.ar(ina,freq3,q) +
				BPF.ar(ina,freq4,q) +
				BPF.ar(ina,freq5,q) +
				BPF.ar(ina,freq6,q) +
				BPF.ar(ina,freq7,q) +
				BPF.ar(ina,freq8,q)
			))/8.0
	)	
}).load(s)
;

SynthDef(\busplay, {
	arg out=0,
	in=0,
	amp=0.5;
	var ina=In.ar(in);
	Out.ar(out,
		amp*ina
	);
}).load(s);


SynthDef(\tester, {
	arg out=0,freq=180,amp=0.1,modu=5;
	var mod = 0.1*SinOsc.kr(modu);
	Out.ar(out,amp*(SinOsc.ar(freq*(1+mod))+SinOsc.ar(2*freq*(1+mod))+SinOsc.ar(3*freq*(1+mod))))
}).load(s);

SynthDef(\simpledijj, {
	arg out=0, freq=80, a=0.5,amp=0.5;
	var numerator   = (1-a)**2,
	    denominator = (1 + a * SinOsc.ar(freq))**2;
	Out.ar(out,
		LeakDC.ar(amp * numerator / denominator)
	);	    
}).load(s);


// { BrownNoise.ar(0.1) }.play;

// \begin{equation}
// dijj(f, a, t) = \frac{(1-a)^2}{((1 +
// a sin(2 \pi f t))^2)}
// \end{equation}
// 

SynthDef(\dijj, {
	arg out=0, freq=80, a=0.5,amp=0.5,noise=0.1,modfreq=1;
	var ka = a, //(1+LFBrownNoise2.kr(freq:modfreq, mul:noise))*a,
	    kfreq = freq*(1+LFBrownNoise2.kr(freq:modfreq,mul:noise)),
	    numerator   = (1.0 - ka)**2,
	    denominator = (1.0 + (ka * SinOsc.ar(kfreq)))**2;
	Out.ar(out,
			LeakDC.ar(amp * numerator / denominator)
	);	    
}).load(s);


b = Bus.audio(s,1);
p = Synth(\pipe,[out: 0,in: b, amp: 0.9, base: 44.0, seperation: 44.0, noise: 0.1,q:0.5]);
// p = Synth(\busplay,[out: 0, in: b, amp:1.0]);
t = Synth(\dijj,[out: b, freq: 120, a: 0.5, amp:2.0]);
bt.set(\noise,0.0001)
s.freqscope
s.scope

// { CombL.ar(WhiteNoise.ar(0.01), 0.01, 0.01, 0.2) }.play;
