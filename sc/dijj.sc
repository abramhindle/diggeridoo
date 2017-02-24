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
	var
	dfreq1=1.0/(base),
	dfreq2=1.0/(1*seperation+base),
	dfreq3=1.0/(2*seperation+base),
	dfreq4=1.0/(3*seperation+base),
	dfreq5=1.0/(4*seperation+base),
	dfreq6=1.0/(5*seperation+base),
	dfreq7=1.0/(6*seperation+base),
	dfreq8=1.0/(7*seperation+base),
	ina=In.ar(in);
	Out.ar(out,
			(amp*(
				CombC.ar(ina,delaytime:dfreq1) +
				CombC.ar(ina,delaytime:dfreq2) +
				CombC.ar(ina,delaytime:dfreq3) +
				CombC.ar(ina,delaytime:dfreq4) +
				CombC.ar(ina,delaytime:dfreq5) +
				CombC.ar(ina,delaytime:dfreq6) +
				CombC.ar(ina,delaytime:dfreq7) +
				CombC.ar(ina,delaytime:dfreq8)
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

// What is the range of spitC and splitL

SynthDef(\dijj, {
	arg out=0, freq=80, a=0.5,amp=0.5,noise=0.001,modfreq=10,spitL=0;
	var ilfo = LeakDC.kr(LFBrownNoise2.kr(freq:freq/100) + LFBrownNoise2.kr(freq:freq/10)),
	    ka = ((ilfo * ((-1) * noise)) + a),
	    ff = freq + ilfo,
	    kfreq = ff,
	    numerator   = (1.0 - ka)**2,
	    denominator = (1.0 + (ka * SinOsc.ar(kfreq)))**2,
	    spitC = BrownNoise.ar(mul:0.1) * spitL,
	    dijjForm = HPF.ar(amp * numerator / denominator, freq);
	Out.ar(out,
			LPF.ar((spitC * dijjForm) + dijjForm, 4000)
	);	    
}).load(s);

SynthDef(\dijj2, {
	arg out=0, freq=80, a=0.5,amp=0.5,noise=0.1,modfreq=10;
	var ka = a, //(1+LFBrownNoise2.kr(freq:modfreq, mul:noise))*a,
	    kfreq = freq*(1+LFBrownNoise2.kr(freq:modfreq,mul:noise)),
	    numerator   = (1.0 - ka)**2,
	    denominator = (1.0 + (ka * SinOsc.ar(kfreq)))**2;
	Out.ar(out,
			LeakDC.ar(amp * numerator / denominator)
	);	    
}).load(s);


~b = Bus.audio(s,1);
~b2 = Bus.audio(s,1);
p = Synth(\combpipe,[out: 0,in: ~b2, amp: 0.9, base: 44.0, seperation: 44.0, noise: 0.1,q:0.5]);
~p2 = Synth(\pipe,[out: ~b2,in: ~b, amp: 0.9, base: 44.0, seperation: 88.0, noise: 0.1,q:0.5]);

// p = Synth(\busplay,[out: 0, in: b, amp:1.0]);
t = Synth(\dijj,[out: ~b, freq: 240, a: 0.5, amp:5.0, noise:0.02]);
t.set(\freq, 45.0*4.0)
t.set(\spitL, 0.3)
t.set(\noise,0.1)
t.set(\a,1.0 - (1.0.linrand))
bt.set(\noise,0.0001)
s.freqscope
s.scope

p.autogui
~p2.autogui
// { CombL.ar(WhiteNoise.ar(0.01), 0.01, 0.01, 0.2) }.play;
