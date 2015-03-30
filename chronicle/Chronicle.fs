module Chronicle

open System

type User = { name: string; trackCount: int }
type Artist = { name: string; mbid: Guid option }
type Track = { name: string; artist: Artist; mbid: Guid option }
type Listen = Track Time.Stamped
