s.boot;

SynthDef(\pipe, {
	arg out=0,
	in=0,
	base=100,//10 to 500
	seperation=100,//10 to 200
	resonance=0.5,
	amp=0.5,
	q=1,
	c=1.414;
	var freq1=base,
	freq2=freq1+seperation,
	freq3=freq2+c*seperation,
	freq4=freq3+c*c*seperation,
	freq5=freq4+c*c*c*seperation,
	freq6=freq5+c*c*c*c*seperation,
	freq7=freq6+c*c*c*c*c*seperation,
	freq8=freq7+c*c*c*c*c*c*seperation,
	ina=In.ar(in);
	Out.ar(out,
		amp*(
			BPF.ar(ina,freq1,q) +
			BPF.ar(ina,freq2,q) +
			BPF.ar(ina,freq3,q) +
			BPF.ar(ina,freq4,q) +
			BPF.ar(ina,freq5,q) +
			BPF.ar(ina,freq6,q) +
			BPF.ar(ina,freq7,q) +
			BPF.ar(ina,freq8,q)
		)/8.0
	)	
}).load(s);

SynthDef(\tester, {
	arg out=0,freq=440,amp=0.1;
	Out.ar(out,amp*(SinOsc.ar(freq)+SinOsc.ar(2*freq)+SinOsc.ar(3*freq)))
}).load(s);
b = Bus.audio(s,1);
t = Synth(\tester,[\out,b]);
p = Synth(\pipe,[\in,b]);
p.autogui